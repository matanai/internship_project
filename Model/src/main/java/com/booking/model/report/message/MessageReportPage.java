package com.booking.model.report.message;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageReportPage {
    private UUID trackingId;
    private String userNameAndRole;
    private LocalDateTime dateTime;
    private Integer numMessages;
    private List<MessageRecord> messageRecordList;
}



