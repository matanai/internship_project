package com.booking.model.util;

import com.booking.model.entity.Hotel;
import com.booking.model.entity.Room;
import com.booking.model.entity.RoomType;
import com.booking.model.entity.Tracking;
import com.booking.model.payload.HotelPayload;

import java.util.Collections;
import java.util.UUID;

import static com.booking.model.payload.HotelPayload.RoomTypePayload;

/**
 * Simple mapper class for mapping data from the {@link HotelPayload} to the corresponding entity in the hotel hierarchy
 * @version 3.0.1
 */
public class PayloadUtil
{
    public static Hotel mapToHotel(HotelPayload hotelPayload, Tracking activated, Tracking deactivated)  {
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelPayload.getHotelId());
        hotel.setHotelName(hotelPayload.getHotelName());
        hotel.setHotelEmail(hotelPayload.getHotelEmail());
        hotel.setHotelPhone(hotelPayload.getHotelPhone());
        hotel.setHotelAddress(hotelPayload.getHotelAddress());
        hotel.setHotelImgFile(hotelPayload.getHotelImgFile());
        hotel.setActivated(activated);
        hotel.setDeactivated(deactivated);
        return hotel;
    }

    public static RoomType mapToRoomType(RoomTypePayload roomTypePayload, Hotel hotel, Tracking activated, Tracking deactivated) {
        RoomType roomType = new RoomType();
        roomType.setHotelId(hotel.getHotelId());
        roomType.setRoomTypeId(roomTypePayload.getRoomTypeId());
        roomType.setRoomTypeName(roomTypePayload.getRoomTypeName());
        roomType.setHasBreakfast(roomTypePayload.isHasBreakfast());
        roomType.setHasRefund(roomTypePayload.isHasRefund());
        roomType.setRoomTypePrice(roomTypePayload.getRoomTypePrice());
        roomType.setMaxGuests(roomTypePayload.getMaxGuests());
        roomType.setRoomTypeImgFile(roomTypePayload.getRoomTypeImgFile());
        roomType.setActivated(activated);
        roomType.setDeactivated(deactivated);
        return roomType;
    }

    public static Room mapToRoom(String roomId, RoomType roomType, Tracking activated, Tracking deactivated) {
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setActivated(activated);
        room.setDeactivated(deactivated);
        return room;
    }

    /**
     * Convenience method
     */
    public static HotelPayload mapToHotelPayload(Hotel hotel, UUID correlationId) {
        HotelPayload hotelPayload = new HotelPayload();
        hotelPayload.setAvailable(true);
        hotelPayload.setCorrelationId(correlationId);
        hotelPayload.setHotelId(hotel.getHotelId());
        hotelPayload.setHotelName(hotel.getHotelName());
        hotelPayload.setHotelEmail(hotel.getHotelEmail());
        hotelPayload.setHotelPhone(hotel.getHotelPhone());
        hotelPayload.setHotelAddress(hotel.getHotelAddress());
        hotelPayload.setHotelImgFile(hotel.getHotelImgFile());
        hotelPayload.setRoomTypeList(Collections.emptyList());
        return hotelPayload;
    }

    /**
     * Convenience method
     */
    public static HotelPayload.RoomTypePayload mapToRoomTypePayload(RoomType roomType) {
        HotelPayload.RoomTypePayload roomTypePayload = new HotelPayload.RoomTypePayload();
        roomTypePayload.setAvailable(true);
        roomTypePayload.setRoomTypeId(roomType.getRoomTypeId());
        roomTypePayload.setRoomTypeName(roomType.getRoomTypeName());
        roomTypePayload.setRoomTypePrice(roomType.getRoomTypePrice());
        roomTypePayload.setMaxGuests(roomType.getMaxGuests());
        roomTypePayload.setHasBreakfast(roomType.isHasBreakfast());
        roomTypePayload.setHasRefund(roomType.isHasRefund());
        roomTypePayload.setRoomTypeImgFile(roomType.getRoomTypeImgFile());
        roomTypePayload.setRoomIdList(Collections.emptyList());
        return roomTypePayload;
    }
}
