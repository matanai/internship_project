package com.booking.model.entity;

import com.booking.model.report.message.RoomDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Room")
@Table(name = "room_tb")
@SqlResultSetMapping(
        name = "roomDetailsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = RoomDetails.class,
                        columns = {
                                @ColumnResult(name = "room_id", type = String.class),
                                @ColumnResult(name = "activated", type = String.class),
                                @ColumnResult(name = "room_type_name", type = String.class)
                        }
                )
        }
)
@NamedNativeQuery(
        name = "Room.getRoomDetails",
        query = "SELECT DISTINCT r.room_id, r.activated, rt.room_type_name FROM room_tb r "
                + "JOIN room_type_tb rt on r.room_type_id = rt.room_type_id "
                + "JOIN hotel_tb h on rt.hotel_id = h.hotel_id "
                + "WHERE h.hotel_id = :hotelId "
                + "AND (r.activated = :tracking OR r.deactivated = :tracking)",
        resultSetMapping = "roomDetailsMapping"
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room implements Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @SequenceGenerator(name = "room_sequence", sequenceName = "room_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "room_type_id")
    private String roomTypeId;

    // FOREIGN REFERENCES

    @ManyToOne
    @JoinColumn(name = "activated", referencedColumnName = "id")
    @JsonIgnore
    private Tracking activated;

    @ManyToOne
    @JoinColumn(name = "deactivated", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE) // Do not throw exceptions if deactivated is null
    @JsonIgnore
    private Tracking deactivated;
}
