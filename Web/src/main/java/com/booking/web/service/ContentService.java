package com.booking.web.service;

import com.booking.model.entity.Hotel;
import com.booking.model.entity.Room;
import com.booking.model.entity.RoomType;
import com.booking.model.repository.HotelRepository;
import com.booking.model.repository.RoomRepository;
import com.booking.model.repository.RoomTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service class to handle viewing and editing hotel and room content
 * @version 3.0.1
 */
@Slf4j
@Service
public class ContentService
{
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final ProducerService producerService;

    @Autowired
    public ContentService(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository,
                          RoomRepository roomRepository, ProducerService producerService) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.producerService = producerService;
    }

    /**
     * Collect hotel and room data for hotels view
     */
    public List<Hotel> prepareContent() {

        List<Hotel> hotelList = hotelRepository.findAllActiveHotels();
        for (Hotel hotel : hotelList) {
            List<RoomType> roomTypeList = roomTypeRepository.findAllActiveRoomTypesByHotelId(hotel.getHotelId());
            hotel.setRoomTypes(roomTypeList);
            for (RoomType roomType : roomTypeList) {
                List<Room> roomList = roomRepository.findAllActiveRoomsByRoomTypeId(roomType.getRoomTypeId());
                roomType.setRooms(roomList);
            }
        }

        hotelList.sort(Comparator.comparing(Hotel::getHotelName));
        return hotelList;
    }
}
