package com.booking.model.repository;

import com.booking.model.entity.Room;
import com.booking.model.entity.Tracking;
import com.booking.model.report.message.RoomDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>
{
    @Query("SELECT r FROM Room r WHERE r.roomTypeId = :roomTypeId AND r.deactivated IS NULL")
    List<Room> findAllActiveRoomsByRoomTypeId(@Param("roomTypeId") String roomTypeId);

    @Query("SELECT r FROM Room r WHERE r.roomId = :roomId AND r.deactivated IS NULL")
    Optional<Room> findActiveRoomByRoomId(@Param("roomId") String roomId);

    @Query(nativeQuery = true)
    List<RoomDetails> getRoomDetails(@Param("hotelId") String hotelId, @Param("tracking") Tracking tracking);
}
