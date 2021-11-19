package com.booking.model.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "Tracking")
@Table(
        name = "tracking_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unq_correlation_id",
                        columnNames = "correlation_id"
                )
        }
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Tracking implements Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @GenericGenerator(
            name = "uuid_sequence",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @GeneratedValue(generator = "uuid_sequence")
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "num_messages", nullable = false)
    private Integer numMessages;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    // FOREIGN REFERENCES

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "tracking")
    @ToString.Exclude
    private List<Message> messages;

    @OneToMany(mappedBy = "activated")
    @ToString.Exclude
    private List<Hotel> activatedHotels;

    @OneToMany(mappedBy = "deactivated")
    @ToString.Exclude
    private List<Hotel> deactivatedHotels;

    @OneToMany(mappedBy = "activated")
    @ToString.Exclude
    private List<RoomType> activatedRoomTypes;

    @OneToMany(mappedBy = "deactivated")
    @ToString.Exclude
    private List<RoomType> deactivatedRoomTypes;

    @OneToMany(mappedBy = "activated")
    @ToString.Exclude
    private List<RoomType> activatedRooms;

    @OneToMany(mappedBy = "deactivated")
    @ToString.Exclude
    private List<RoomType> deactivatedRooms;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tracking tracking = (Tracking) o;
        return id.equals(tracking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
