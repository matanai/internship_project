package com.booking.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Hotel")
@Table(name = "hotel_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel implements Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @SequenceGenerator(name = "hotel_sequence", sequenceName = "hotel_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    @Column(name = "hotel_name", nullable = false)
    @NotEmpty
    private String hotelName;

    @Column(name = "hotel_email", nullable = false)
    @NotEmpty
    private String hotelEmail;

    @Column(name = "hotel_phone", nullable = false)
    @NotEmpty
    private String hotelPhone;

    @Column(name = "hotel_address", nullable = false)
    @NotEmpty
    private String hotelAddress;

    @Column(name = "hotel_img_file", nullable = false)
    @NotEmpty
    private String hotelImgFile;

    // FOREIGN REFERENCES

    @OneToMany
    @NotFound(action = NotFoundAction.IGNORE)
    private List<RoomType> roomTypes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "activated", referencedColumnName = "id")
    @JsonIgnore
    private Tracking activated;

    @ManyToOne
    @JoinColumn(name = "deactivated", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE) // Do not throw exceptions if deactivated is null
    @JsonIgnore
    private Tracking deactivated;

    // EQUALS AND HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return hotelName.equals(hotel.hotelName)
                && hotelEmail.equals(hotel.hotelEmail)
                && hotelPhone.equals(hotel.hotelPhone)
                && hotelAddress.equals(hotel.hotelAddress)
                && hotelImgFile.equals(hotel.hotelImgFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelName, hotelEmail, hotelPhone, hotelAddress, hotelImgFile);
    }

    @Override
    public String toString() {
        return String.format("'%s' (%s, %s, %s)",
                hotelName, hotelAddress, hotelEmail, hotelPhone);
    }
}
