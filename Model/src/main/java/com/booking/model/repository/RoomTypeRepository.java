package com.booking.model.repository;

import com.booking.model.entity.RoomType;
import com.booking.model.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long>
{
    @Query("SELECT count(rt) FROM RoomType rt WHERE rt.roomTypeId = :roomTypeId")
    Integer checkIfRoomTypeExists(@Param("roomTypeId") String roomTypeId);

    @Query("SELECT rt FROM RoomType rt WHERE rt.hotelId = :hotelId AND rt.deactivated IS NULL")
    List<RoomType> findAllActiveRoomTypesByHotelId(@Param("hotelId") String hotelId);

    @Query("SELECT rt FROM RoomType rt WHERE rt.roomTypeId = :roomTypeId AND rt.deactivated IS NULL")
    Optional<RoomType> findActiveRoomTypeByRoomTypeId(@Param("roomTypeId") String roomTypeId);

    @Query(value = "SELECT DISTINCT room_type_id FROM room_type_tb WHERE hotel_id = :hotelId AND (activated = :tracking OR deactivated = :tracking)", nativeQuery = true)
    List<String> getAllRoomTypeIdByHotelIdAndTrackingId(@Param("hotelId") String hotelId, @Param("tracking") Tracking tracking);

    @Query("SELECT DISTINCT rt FROM RoomType rt WHERE rt.roomTypeId = :roomTypeId AND (rt.activated = :tracking OR rt.deactivated = :tracking)")
    List<RoomType> getAllRoomTypesByRoomTypeIdAndTrackingId(@Param("roomTypeId") String roomTypeId, @Param("tracking") Tracking tracking);

    @Query(value = "SELECT activated FROM room_type_tb WHERE room_type_id = :roomTypeId ORDER BY id LIMIT 1", nativeQuery = true)
    Tracking getFirstRoomTypeRecord(@Param("roomTypeId") String roomTypeId);

    Optional<RoomType> findFirstByRoomTypeId(@Param("roomTypeId") String roomTypeId);
}
