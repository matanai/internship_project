package com.booking.consumer.service;

import com.booking.model.entity.*;
import com.booking.model.payload.HotelPayload;
import com.booking.model.repository.*;
import com.booking.model.util.PayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.booking.model.payload.HotelPayload.RoomTypePayload;

/**
 * Service class for accepting messages from message broker and updating database entities accordingly. Messages arrive
 * in the form of a {@link HotelPayload} object, carrying data on hotels and rooms. All actions are done in transaction.
 *
 * @version 3.0.1
 */
@Slf4j
@Service
public class ConsumerService
{
    public static final String MSG_HOTEL_IS_NULL = "Hotel payload is null or does not contain the correlation id";
    public static final String MSG_NO_CORRELATION = "No tracking record with matching correlation id found";
    public static final String MSG_DEACTIVATE_NON_EXISTING_HOTEL = "Can't deactivate non-existing hotel";
    public static final String MSG_DEACTIVATE_NON_EXISTING_ROOM_TYPE = "Can't deactivate non-existing room type";
    public static final String MSG_HOTEL_ALREADY_DEACTIVATED = "Hotel has already been deactivated";
    public static final String MSG_ROOM_TYPE_ALREADY_DEACTIVATED = "Room type has already been deactivated";

    private final TrackingRepository trackingRepository;
    private final MessageRepository messageRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public ConsumerService(TrackingRepository trackingRepository, MessageRepository messageRepository,
                           HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository) {
        this.trackingRepository = trackingRepository;
        this.messageRepository = messageRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Process message in transaction
     */
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            noRollbackFor = RepositoryException.class)
    public void processMessage(final HotelPayload hotelPayload) throws TransactionException {
        if (hotelPayload == null || hotelPayload.getCorrelationId() == null) {
            throw new TransactionException(MSG_HOTEL_IS_NULL);
        }

        Tracking tracking = trackingRepository.findTrackingByCorrelationId(hotelPayload.getCorrelationId())
                .orElseThrow(() -> new TransactionException(MSG_NO_CORRELATION));

        boolean isSuccessful = true;
        String hotelId = hotelPayload.getHotelId();

        try {
            boolean hotelExists = hotelRepository.checkIfHotelExists(hotelId) > 0;

            if (!hotelPayload.isAvailable()) {
                if (!hotelExists) {
                    throw new TransactionException(MSG_DEACTIVATE_NON_EXISTING_HOTEL);
                }

                this.deactivateHotel(tracking, hotelPayload);

            } else {
                if (!hotelExists) {
                    this.activateHotel(tracking, hotelPayload);
                } else {
                    this.updateHotel(tracking, hotelPayload);
                }
            }
        } catch (RepositoryException e) {
            log.error("{}", e.getMessage());
            isSuccessful = false;
        }

        this.createNewMessage(hotelId, tracking, isSuccessful);
        log.info("Update transaction completed");
    }

    /**
     * Deactivate hotel and all associated rooms and room types
     */
    private void deactivateHotel(Tracking tracking, HotelPayload hotelPayload) throws RepositoryException {
        Hotel hotel = hotelRepository.findActiveHotelByHotelId(hotelPayload.getHotelId())
                .orElseThrow(() -> new RepositoryException(MSG_HOTEL_ALREADY_DEACTIVATED));

        hotel.setDeactivated(tracking);
        hotelRepository.save(hotel);

        roomTypeRepository.findAllActiveRoomTypesByHotelId(hotel.getHotelId()).forEach(roomType -> {
            roomType.setDeactivated(tracking);
            roomTypeRepository.save(roomType);

            roomRepository.findAllActiveRoomsByRoomTypeId(roomType.getRoomTypeId()).forEach(room -> {
                room.setDeactivated(tracking);
                roomRepository.save(room);
            });
        });
    }

    /**
     * Activate hotel and all associated rooms and room types
     */
    private void activateHotel(Tracking tracking, HotelPayload hotelPayload) throws RepositoryException {
        Hotel hotel = PayloadUtil.mapToHotel(hotelPayload, tracking, null);
        hotelRepository.save(hotel);

        hotelPayload.getRoomTypeList().forEach(roomTypePayload -> {
            if (!roomTypePayload.isAvailable()) {
                throw new RepositoryException(MSG_DEACTIVATE_NON_EXISTING_ROOM_TYPE);
            }

            activateRoomsAndRoomTypes(tracking, roomTypePayload, hotel);
        });
    }

    /**
     * Update hotel and all associated rooms and room types
     */
    private void updateHotel(Tracking tracking, HotelPayload hotelPayload) throws RepositoryException {
        Hotel newHotel = PayloadUtil.mapToHotel(hotelPayload, tracking, null);

        Optional<Hotel> optionalOldHotel = hotelRepository.findActiveHotelByHotelId(newHotel.getHotelId());

        if (optionalOldHotel.isPresent()) {
            Hotel oldHotel = optionalOldHotel.get();

            if (!newHotel.equals(oldHotel)) { // Update
                oldHotel.setDeactivated(tracking);
                hotelRepository.saveAll(Arrays.asList(oldHotel, newHotel));
            }

            hotelPayload.getRoomTypeList().forEach(roomTypePayload -> {
                if (!roomTypePayload.isAvailable()) {
                    deactivateRoomsAndRoomTypes(tracking, roomTypePayload);
                } else {
                    updateRoomsAndRoomTypes(tracking, roomTypePayload, newHotel);
                }
            });

        } else { // Reactivate
            hotelRepository.save(newHotel);
            hotelPayload.getRoomTypeList().forEach(roomTypePayload -> {
                if (!roomTypePayload.isAvailable()) {
                    throw new RepositoryException(MSG_ROOM_TYPE_ALREADY_DEACTIVATED);
                }

                activateRoomsAndRoomTypes(tracking, roomTypePayload, newHotel);
            });
        }
    }

    /**
     * Deactivate rooms and room types
     */
    private void deactivateRoomsAndRoomTypes(Tracking tracking, RoomTypePayload roomTypePayload) throws RepositoryException {
        List<String> roomIdList = roomTypePayload.getRoomIdList();

        if (roomIdList.size() == 0) { // Deactivate room type along with all associated rooms
            if (roomTypeRepository.checkIfRoomTypeExists(roomTypePayload.getRoomTypeId()) == 0) {
                throw new RepositoryException(MSG_DEACTIVATE_NON_EXISTING_ROOM_TYPE);
            }

            RoomType roomType = roomTypeRepository.findActiveRoomTypeByRoomTypeId(roomTypePayload.getRoomTypeId())
                    .orElseThrow(() -> new RepositoryException(MSG_ROOM_TYPE_ALREADY_DEACTIVATED));

            roomType.setDeactivated(tracking);
            roomTypeRepository.save(roomType);

            roomRepository.findAllActiveRoomsByRoomTypeId(roomType.getRoomTypeId()).forEach(room -> {
                room.setDeactivated(tracking);
                roomRepository.save(room);
            });

        } else { // Deactivate only rooms specified in the list
            roomIdList.forEach(roomId -> roomRepository.findActiveRoomByRoomId(roomId).ifPresent(room -> {
                room.setDeactivated(tracking);
                roomRepository.save(room);
            }));
        }
    }

    /**
     * Activate or reactivate room type
     */
    private void activateRoomsAndRoomTypes(Tracking tracking, RoomTypePayload roomTypePayload, Hotel hotel) throws RepositoryException {
        RoomType roomType = PayloadUtil.mapToRoomType(roomTypePayload, hotel, tracking, null);
        roomTypeRepository.save(roomType);

        roomTypePayload.getRoomIdList().forEach(roomId -> {
            Room room = PayloadUtil.mapToRoom(roomId, roomType, tracking, null);
            roomRepository.save(room);
        });
    }

    /**
     * Update rooms and room types
     */
    private void updateRoomsAndRoomTypes(Tracking tracking, RoomTypePayload roomTypePayload, Hotel hotel) throws RepositoryException {
        RoomType newRoomType = PayloadUtil.mapToRoomType(roomTypePayload, hotel, tracking, null);
        Optional<RoomType> optionalOldRoomType = roomTypeRepository.findActiveRoomTypeByRoomTypeId(newRoomType.getRoomTypeId());

        if (optionalOldRoomType.isPresent()) {
            RoomType oldRoomType = optionalOldRoomType.get();

            if (!newRoomType.equals(oldRoomType)) {
                oldRoomType.setDeactivated(tracking);
                roomTypeRepository.saveAll(Arrays.asList(oldRoomType, newRoomType));
            }

            roomTypePayload.getRoomIdList().forEach(roomId -> {
                Optional<Room> optionalRoom = roomRepository.findActiveRoomByRoomId(roomId);
                if (!optionalRoom.isPresent()) {
                    Room room = PayloadUtil.mapToRoom(roomId, newRoomType, tracking, null);
                    roomRepository.save(room);
                }
            });

        } else { // If no room type was found, activate new one
            activateRoomsAndRoomTypes(tracking, roomTypePayload, hotel);
        }
    }

    /**
     * Create new message
     */
    private void createNewMessage(String hotelId, Tracking tracking, boolean isSuccessful) {
        Message message = Message.builder()
                .dateTime(LocalDateTime.now())
                .isSuccessful(isSuccessful)
                .tracking(tracking)
                .hotelId(hotelId)
                .build();

        messageRepository.save(message);
    }
}
