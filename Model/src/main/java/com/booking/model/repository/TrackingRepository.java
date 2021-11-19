package com.booking.model.repository;

import com.booking.model.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, UUID>
{
    @Query("SELECT count(t) FROM Tracking t WHERE t.correlationId = :correlationId")
    Integer checkCorrelationId(@Param("correlationId") UUID correlationId);

    Optional<Tracking> findTrackingByCorrelationId(@Param("correlationId") UUID correlationId);

    @Query(value = "SELECT * FROM tracking_tb ORDER BY date_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Tracking> getAllWithLimitAndOffset(@Param("limit") int limit, @Param("offset") int offset);
}
