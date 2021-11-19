package com.booking.model.report.message;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRecord
{
    private String hotelName;
    private Boolean isSuccessful;
    private LocalDateTime dateTime;
    private List<Action> actionList;
}
