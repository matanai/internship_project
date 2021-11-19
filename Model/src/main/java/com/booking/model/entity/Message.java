package com.booking.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "Message")
@Table(name = "message_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Message implements Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @SequenceGenerator(name = "message_sequence", sequenceName = "message_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "is_successful", nullable = false)
    private boolean isSuccessful;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    // FOREIGN REFERENCES

    @OneToMany
    @JoinColumn(name = "hotel_id", referencedColumnName = "hotel_id")
    @ToString.Exclude
    private List<Hotel> hotels;

    @ManyToOne
    @JoinColumn(name = "correlation_id", referencedColumnName = "correlation_id")
    @ToString.Exclude
    private Tracking tracking;

}
