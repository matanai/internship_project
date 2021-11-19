package com.booking.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "RoomType")
@Table(name = "room_type_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomType implements Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @SequenceGenerator(name = "room_type_sequence", sequenceName = "room_type_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_type_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "room_type_id", nullable = false)
    private String roomTypeId;

    @Column(name = "hotel_id")
    private String hotelId;

    @Column(name = "room_type_name", nullable = false)
    private String roomTypeName;

    @Column(name = "room_type_price", nullable = false)
    private BigDecimal roomTypePrice;

    @Column(name = "has_breakfast", nullable = false)
    private boolean hasBreakfast;

    @Column(name = "has_refund", nullable = false)
    private boolean hasRefund;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "room_type_img_file", nullable = false)
    private String roomTypeImgFile;

    // FOREIGN REFERENCES

    @OneToMany
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Room> rooms = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "activated", referencedColumnName = "id")
    @JsonIgnore
    private Tracking activated;

    @ManyToOne
    @JoinColumn(name = "deactivated", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Tracking deactivated;

    // EQUALS AND HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomType roomType = (RoomType) o;
        return hasBreakfast == roomType.hasBreakfast
                && hasRefund == roomType.hasRefund
                && roomTypeName.equals(roomType.roomTypeName)
                && roomTypePrice.equals(roomType.roomTypePrice)
                && maxGuests.equals(roomType.maxGuests)
                && roomTypeImgFile.equals(roomType.roomTypeImgFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomTypeName, roomTypePrice, hasBreakfast, hasRefund, maxGuests, roomTypeImgFile);
    }

    @Override
    public String toString() {
        return String.format("'%s' ($%s, %s guests%s%s)",
                roomTypeName, roomTypePrice, maxGuests,
                hasBreakfast ? ", breakfast included" : "",
                hasRefund ? ", refundable" : "");
    }
}
