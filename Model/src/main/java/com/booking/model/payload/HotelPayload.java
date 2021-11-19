package com.booking.model.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Payload class for sending data to message broker
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HotelPayload {
    private UUID correlationId;
    private boolean available;
    private String hotelId;
    private String hotelName;
    private String hotelEmail;
    private String hotelPhone;
    private String hotelAddress;
    private String hotelImgFile;
    private List<RoomTypePayload> roomTypeList;

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    @ToString
    public static class RoomTypePayload {
        private boolean available;
        private String roomTypeId;
        private String roomTypeName;
        private BigDecimal roomTypePrice;
        private Integer maxGuests;
        private boolean hasBreakfast;
        private boolean hasRefund;
        private String roomTypeImgFile;
        private List<String> roomIdList;
    }
}
