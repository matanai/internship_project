package com.booking.model.repository;

import com.booking.model.entity.Message;
import com.booking.model.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>
{
    @Query("SELECT count(m) FROM Message m WHERE m.tracking = :tracking AND m.isSuccessful = TRUE")
    Integer countSuccessfulMessagesByTracking(@Param("tracking") Tracking tracking);
}
