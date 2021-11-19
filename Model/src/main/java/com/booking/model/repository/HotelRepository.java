package com.booking.model.repository;

import com.booking.model.entity.Hotel;
import com.booking.model.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>
{
    @Query("SELECT count(h) FROM Hotel h WHERE h.hotelId = :hotelId")
    Integer checkIfHotelExists(@Param("hotelId") String hotelId);

    @Query("SELECT h FROM Hotel h WHERE h.hotelId = :hotelId AND h.deactivated IS NULL")
    Optional<Hotel> findActiveHotelByHotelId(@Param("hotelId") String hotelId);

    @Query("SELECT h.hotelName FROM Hotel h WHERE h.hotelId = :hotelId ORDER BY h.id DESC")
    List<String> getAllHotelNamesByHotelId(@Param("hotelId") String hotelId);

    @Query("SELECT h FROM Hotel h WHERE h.hotelId = :hotelId AND (h.activated = :tracking OR h.deactivated = :tracking)")
    List<Hotel> findAllByHotelIdAndTracking(@Param("hotelId") String hotelId, @Param("tracking") Tracking tracking);

    Optional<Hotel> findFirstByHotelId(@Param("hotelId") String hotelId);

    @Query("FROM Hotel h WHERE h.deactivated IS NULL")
    List<Hotel> findAllActiveHotels();
}
