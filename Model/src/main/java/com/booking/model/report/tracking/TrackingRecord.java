package com.booking.model.report.tracking;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingRecord 
{
    private UUID trackingId;
    private UUID correlationId;
    private String userNameAndRole;
    private Integer numMessages;
    private Integer successRate;
    private LocalDateTime dateTime;
}
