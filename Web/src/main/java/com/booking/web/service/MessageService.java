package com.booking.web.service;

import com.booking.model.entity.*;
import com.booking.model.report.message.Action;
import com.booking.model.report.message.RoomDetails;
import com.booking.model.report.message.MessageRecord;
import com.booking.model.report.message.MessageReportPage;
import com.booking.model.repository.HotelRepository;
import com.booking.model.repository.RoomRepository;
import com.booking.model.repository.RoomTypeRepository;
import com.booking.model.repository.TrackingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.booking.model.report.message.Action.*;

@Slf4j
@Service
public class MessageService
{
    private static final String HOTEL_ACTION = "Hotel %s %s";
    private static final String ROOM_TYPE_ACTION = "Room type %s %s";
    private static final String ROOM_ACTION = "%s rooms %s ('%s')";

    private static final String NO_CORRESPONDING_TRACKING_ID = "No corresponding tracking id was found";
    private static final String INVALID_HOTEL_NAME = "No hotel name was found";

    private final TrackingRepository trackingRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public MessageService(TrackingRepository trackingRepository, HotelRepository hotelRepository,
                          RoomTypeRepository roomTypeRepository, RoomRepository roomRepository) {
        this.trackingRepository = trackingRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Prepare message report page
     */
    public MessageReportPage prepareMessageReport(String trackingId, String userNameAndRole) throws ServiceException {
        Tracking tracking = trackingRepository.findById(UUID.fromString(trackingId))
                .orElseThrow(() -> new ServiceException(NO_CORRESPONDING_TRACKING_ID));

        List<MessageRecord> messageRecordList = new ArrayList<>();
        List<Message> messageList = tracking.getMessages();

        for (Message message : messageList) {

            List<Action> actionList = this.getHotelActions(message.getHotelId(), tracking);
            actionList.addAll(this.getRoomTypeActions(message.getHotelId(), tracking));
            actionList.addAll(this.getRoomActionsByHotelIdAndTrackingId(message.getHotelId(), tracking));

            if (!actionList.isEmpty()) {
                MessageRecord messageRecord = MessageRecord.builder()
                        .hotelName(hotelRepository.getAllHotelNamesByHotelId(message.getHotelId())
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new ServiceException(INVALID_HOTEL_NAME)))
                        .dateTime(message.getDateTime())
                        .isSuccessful(message.isSuccessful())
                        .actionList(actionList)
                        .build();

                messageRecordList.add(messageRecord);
            }
        }

        return MessageReportPage.builder()
                .trackingId(tracking.getId())
                .dateTime(tracking.getDateTime())
                .numMessages(tracking.getNumMessages())
                .messageRecordList(messageRecordList)
                .userNameAndRole(userNameAndRole)
                .build();
    }

    /**
     * Get hotel actions
     */
    private List<Action> getHotelActions(String hotelId, Tracking tracking) {
        List<Action> hotelActionsList = new ArrayList<>();
        List<Hotel> hotelList = hotelRepository.findAllByHotelIdAndTracking(hotelId, tracking);

        if (hotelList.size() == 1) {
            Hotel hotel = hotelList.get(0);

            if (tracking.equals(hotel.getDeactivated()) && !tracking.equals(hotel.getActivated())) { // Hotel was deactivated
                hotelActionsList.add(new Action(String.format(HOTEL_ACTION, "'" + hotel.getHotelName() + "'", "removed"), ActionColor.REMOVE));

            } else {
                Hotel firstActivated = hotelRepository.findFirstByHotelId(hotel.getHotelId())
                        .orElseThrow(() -> new ServiceException("No first hotel record found"));

                hotelActionsList.add((tracking.equals(firstActivated.getActivated()))
                        ? new Action(String.format(HOTEL_ACTION, hotel, "added"), ActionColor.ADD)
                        : new Action(String.format(HOTEL_ACTION, hotel, "reactivated"), ActionColor.ADD));
            }

        } else if (hotelList.size() == 2) {
            hotelActionsList.addAll(this.getHotelUpdatesList(hotelList, tracking));
        }

        return hotelActionsList;
    }

    /**
     * If action has changed hotel, get list of what was updated
     */
    private List<Action> getHotelUpdatesList(List<Hotel> hotelList, Tracking tracking) {
        List<Action> hotelUpdatesList = new ArrayList<>();

        Hotel newHotel, oldHotel;
        if (hotelList.get(0).getActivated().equals(tracking)) {
            newHotel = hotelList.get(0);
            oldHotel = hotelList.get(1);
        } else {
            newHotel = hotelList.get(1);
            oldHotel = hotelList.get(0);
        }

        hotelUpdatesList.add(new Action(String.format(HOTEL_ACTION, "'" + newHotel.getHotelName() + "'", "updated"), ActionColor.UPDATE));

        if (!newHotel.getHotelName().equals(oldHotel.getHotelName())) {
            hotelUpdatesList.add(new Action(String.format("* name '%s' -> '%s'%n", oldHotel.getHotelName(), newHotel.getHotelName()), ActionColor.UPDATE));
        }

        if (!newHotel.getHotelAddress().equals(oldHotel.getHotelAddress())) {
            hotelUpdatesList.add(new Action(String.format("* address '%s' -> '%s'%n", oldHotel.getHotelAddress(), newHotel.getHotelAddress()), ActionColor.UPDATE));
        }

        if (!newHotel.getHotelEmail().equals(oldHotel.getHotelEmail())) {
            hotelUpdatesList.add(new Action(String.format("* email '%s' -> '%s'%n", oldHotel.getHotelEmail(), newHotel.getHotelEmail()), ActionColor.UPDATE));
        }

        if (!newHotel.getHotelPhone().equals(oldHotel.getHotelPhone())) {
            hotelUpdatesList.add(new Action(String.format("* phone '%s' -> '%s'%n", oldHotel.getHotelPhone(), newHotel.getHotelPhone()), ActionColor.UPDATE));
        }

        if (!newHotel.getHotelImgFile().equals(oldHotel.getHotelImgFile())) {
            hotelUpdatesList.add(new Action(String.format("* image '%s' -> '%s'%n", oldHotel.getHotelImgFile(), newHotel.getHotelImgFile()), ActionColor.UPDATE));
        }

        return hotelUpdatesList;
    }

    /**
     * Get room type actions
     */
    private List<Action> getRoomTypeActions(String hotelId, Tracking tracking) {
        List<Action> roomTypeActionsList = new ArrayList<>();

        List<String> roomTypeIdList = roomTypeRepository.getAllRoomTypeIdByHotelIdAndTrackingId(hotelId, tracking);

        for (String roomTypeId : roomTypeIdList) {

            List<RoomType> roomTypeList = roomTypeRepository.getAllRoomTypesByRoomTypeIdAndTrackingId(roomTypeId, tracking);

            if (roomTypeList.size() == 1) {
                RoomType roomType = roomTypeList.get(0);

                if (tracking.equals(roomType.getDeactivated()) && !tracking.equals(roomType.getActivated())) {
                    roomTypeActionsList.add(new Action(String.format(ROOM_TYPE_ACTION, "'" + roomType.getRoomTypeName() + "'", "removed"), ActionColor.REMOVE));

                } else {
                    RoomType firstActivated = roomTypeRepository.findFirstByRoomTypeId(roomType.getRoomTypeId())
                            .orElseThrow(() -> new ServiceException("No first room type record found"));
                    roomTypeActionsList.add((tracking.equals(firstActivated.getActivated()))
                            ? new Action(String.format(ROOM_TYPE_ACTION, roomType, "added"), ActionColor.ADD)
                            : new Action(String.format(ROOM_TYPE_ACTION, roomType, "reactivated"), ActionColor.ADD));
                }

            } else if (roomTypeList.size() == 2) {
                roomTypeActionsList.addAll(this.getRoomTypeUpdatesList(roomTypeList, tracking));
            }
        }

        return roomTypeActionsList;

    }

    /**
     * If action has changed room type, get list of what was updated
     */
    private List<Action> getRoomTypeUpdatesList(List<RoomType> roomTypeList, Tracking tracking) {
        List<Action> roomTypeActionsList = new ArrayList<>();

        RoomType newRoomType, oldRoomType;
        if (roomTypeList.get(0).getActivated().equals(tracking)) {
            newRoomType = roomTypeList.get(0);
            oldRoomType = roomTypeList.get(1);
        } else {
            newRoomType = roomTypeList.get(1);
            oldRoomType = roomTypeList.get(0);
        }

        roomTypeActionsList.add(new Action(String.format(ROOM_TYPE_ACTION, "'" + newRoomType.getRoomTypeName() + "'", "updated"), ActionColor.UPDATE));

        if (!newRoomType.getRoomTypeName().equals(oldRoomType.getRoomTypeName())) {
            roomTypeActionsList.add(new Action(String.format("* name '%s' -> '%s'%n", oldRoomType.getRoomTypeName(), newRoomType.getRoomTypeName()), ActionColor.UPDATE));
        }

        if (!newRoomType.getRoomTypePrice().equals(oldRoomType.getRoomTypePrice())) {
            roomTypeActionsList.add(new Action(String.format("* price '$%s' -> '$%s'%n", oldRoomType.getRoomTypePrice(), newRoomType.getRoomTypePrice()), ActionColor.UPDATE));
        }

        if (!newRoomType.getMaxGuests().equals(oldRoomType.getMaxGuests())) {
            roomTypeActionsList.add(new Action(String.format("* max guests '%s' -> '%s'%n", oldRoomType.getMaxGuests(), newRoomType.getMaxGuests()), ActionColor.UPDATE));
        }

        if (newRoomType.isHasBreakfast() != oldRoomType.isHasBreakfast()) {
            roomTypeActionsList.add(new Action(String.format("* has breakfast '%s' -> '%s'%n", oldRoomType.isHasBreakfast(), newRoomType.isHasBreakfast()), ActionColor.UPDATE));
        }

        if (newRoomType.isHasRefund() != oldRoomType.isHasRefund()) {
            roomTypeActionsList.add(new Action(String.format("* has refund '%s' -> '%s'%n", oldRoomType.isHasRefund(), newRoomType.isHasRefund()), ActionColor.UPDATE));
        }

        if (!newRoomType.getRoomTypeImgFile().equals(oldRoomType.getRoomTypeImgFile())) {
            roomTypeActionsList.add(new Action(String.format("* image '%s' -> '%s'%n", oldRoomType.getRoomTypeImgFile(), newRoomType.getRoomTypeImgFile()), ActionColor.UPDATE));
        }

        return roomTypeActionsList;

    }

    private List<Action> getRoomActionsByHotelIdAndTrackingId(String hotelId, Tracking tracking) {
        Map<String, Integer> roomsAddedMap = new HashMap<>();
        Map<String, Integer> roomsRemovedMap = new HashMap<>();

        for (RoomDetails details : roomRepository.getRoomDetails(hotelId, tracking)) {
            if (details.getActivated().equals(tracking.getId().toString())) {
                roomsAddedMap.merge(details.getRoomTypeName(), 1, (v1, v2) -> ++v1);
            } else {
                roomsRemovedMap.merge(details.getRoomTypeName(), 1, (v1, v2) -> ++v1);
            }
        }

        List<Action> roomActionsList = roomsAddedMap.entrySet()
                .stream()
                .map(e -> new Action(String.format(ROOM_ACTION, e.getValue(), "added", e.getKey()), ActionColor.ADD))
                .collect(Collectors.toList());

        roomActionsList.addAll(roomsRemovedMap.entrySet()
                .stream()
                .map(e -> new Action(String.format(ROOM_ACTION, e.getValue(), "removed", e.getKey()), ActionColor.REMOVE))
                .collect(Collectors.toList()));

        return roomActionsList;
    }
}