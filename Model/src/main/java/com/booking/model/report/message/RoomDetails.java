package com.booking.model.report.message;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDetails
{
    private String roomId;
    private String activated;
    private String roomTypeName;
}
