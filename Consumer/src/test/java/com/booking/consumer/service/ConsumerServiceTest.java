package com.booking.consumer.service;

import com.booking.model.entity.*;
import com.booking.model.payload.HotelPayload;
import com.booking.model.repository.*;
import com.booking.model.util.UUIDGeneratorUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.booking.model.util.PayloadUtil.mapToHotelPayload;
import static com.booking.model.util.PayloadUtil.mapToRoomTypePayload;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

/**
 * Simple test class for testing {@link ConsumerService#processMessage(HotelPayload)}
 * @version 3.0.1
 */
@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest
{
    // Test subject
    private ConsumerService consumerService;

    @Mock private TrackingRepository trackingRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private RoomTypeRepository roomTypeRepository;
    @Mock private RoomRepository roomRepository;

    private UUID correlationId;
    private Tracking newTracking, oldTracking;

    private static Hotel hotelExpected;
    private static RoomType roomTypeExpected;

    /**
     * Set our base entities for testing before the test sequence start.
     */
    @BeforeAll
    static void beforeAll() {
        hotelExpected = Hotel.builder()
                .hotelId("hotel_1")
                .hotelName("Zen Princess")
                .hotelAddress("Kemer, Turkey")
                .hotelEmail("zen@company.tk")
                .hotelPhone("39557594048")
                .hotelImgFile("C:\\img\\hotel.png")
                .build();

        roomTypeExpected = RoomType.builder()
                .roomTypeId("room_type_1_1")
                .hotelId("hotel_1")
                .roomTypeName("Single Standard")
                .roomTypePrice(new BigDecimal("80.00"))
                .hasBreakfast(false)
                .hasRefund(false)
                .maxGuests(1)
                .roomTypeImgFile("C:\\img\\room_type.png")
                .build();
    }

    /**
     * Each test starts with refreshing all repositories, as well as generating new correlation id and two sets of
     * tracking objects. Since consumer method starts with checking that the same correlation id is present both in
     * hotel payload, as well as in the tracking table, we must also set tracking mock
     */
    @BeforeEach
    void setUp() {
        consumerService = new ConsumerService(trackingRepository, messageRepository,
                hotelRepository, roomTypeRepository, roomRepository);

        correlationId = UUIDGeneratorUtil.getUUID();

        newTracking = Tracking.builder()
                .id(UUIDGeneratorUtil.getUUID())
                .correlationId(correlationId)
                .dateTime(LocalDateTime.now())
                .user(new User())
                .numMessages(1)
                .build();

        oldTracking = Tracking.builder()
                .correlationId(UUIDGeneratorUtil.getUUID())
                .id(UUIDGeneratorUtil.getUUID())
                .dateTime(LocalDateTime.now())
                .numMessages(1)
                .user(new User())
                .build();

        given(trackingRepository.findTrackingByCorrelationId(correlationId)).willReturn(Optional.of(newTracking));
    }

    /**
     * After each test we check if a message was created and saved only once, status is successful, and tracking id
     * corresponds to the one used in the test
     */
    @AfterEach
    void tearDown(TestInfo info) {
        if (info.getTags().isEmpty()) {
            ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
            verify(messageRepository, times(1)).save(messageArgumentCaptor.capture());
            assertThat(messageArgumentCaptor.getValue().getTracking()).isEqualTo(newTracking);
            assertTrue(messageArgumentCaptor.getValue().isSuccessful());
        }
    }

    @Test
    void testActivateHotel() {
        HotelPayload hotelPayload = mapToHotelPayload(hotelExpected, correlationId);
        HotelPayload.RoomTypePayload roomTypePayload = mapToRoomTypePayload(roomTypeExpected);
        hotelPayload.setRoomTypeList(Collections.singletonList(roomTypePayload));

        consumerService.processMessage(hotelPayload);

        ArgumentCaptor<Hotel> hotelArgumentCaptor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository, times(1)).save(hotelArgumentCaptor.capture());

        ArgumentCaptor<RoomType> roomTypeArgumentCaptor = ArgumentCaptor.forClass(RoomType.class);
        verify(roomTypeRepository, times(1)).save(roomTypeArgumentCaptor.capture());

        assertThat(hotelArgumentCaptor.getValue().getHotelName()).isEqualTo(hotelExpected.getHotelName());
        assertThat(hotelArgumentCaptor.getValue().getActivated()).isEqualTo(newTracking);
        assertThat(hotelArgumentCaptor.getValue().getDeactivated()).isNull();

        assertThat(roomTypeArgumentCaptor.getValue().getRoomTypeName()).isEqualTo(roomTypeExpected.getRoomTypeName());
        assertThat(roomTypeArgumentCaptor.getValue().getActivated()).isEqualTo(newTracking);
        assertThat(roomTypeArgumentCaptor.getValue().getDeactivated()).isNull();
    }

    @Test
    @SuppressWarnings("all")
    void testUpdateHotel() {
        HotelPayload hotelPayload = mapToHotelPayload(hotelExpected, correlationId);
        hotelPayload.setHotelName("Zen Princess SPA");
        hotelPayload.setHotelAddress("Antalya, Turkey");

        given(hotelRepository.checkIfHotelExists(anyString())).willReturn(1);

        hotelExpected.setActivated(oldTracking);
        given(hotelRepository.findActiveHotelByHotelId(anyString())).willReturn(Optional.of(hotelExpected));

        consumerService.processMessage(hotelPayload);

        ArgumentCaptor<List<Hotel>> hotelArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(hotelRepository, times(1)).saveAll(hotelArgumentCaptor.capture());

        Hotel oldHotelActual = hotelArgumentCaptor.getAllValues()
                .stream()
                .flatMap(Collection::stream)
                .filter(e -> e.getDeactivated() != null)
                .findAny()
                .orElse(new Hotel());

        Hotel newHotelActual = hotelArgumentCaptor.getAllValues()
                .stream()
                .flatMap(Collection::stream)
                .filter(e -> e.getDeactivated() == null)
                .findAny()
                .orElse(new Hotel());

        assertThat(oldHotelActual.getActivated()).isEqualTo(hotelExpected.getActivated()).isEqualTo(oldTracking);
        assertThat(newHotelActual.getActivated()).isEqualTo(hotelExpected.getDeactivated()).isEqualTo(newTracking);
        assertThat(newHotelActual.getHotelName()).isEqualTo(hotelPayload.getHotelName());
        assertThat(newHotelActual.getHotelAddress()).isEqualTo(hotelPayload.getHotelAddress());
        assertThat(newHotelActual.getDeactivated()).isNull();

        verify(roomTypeRepository, never()).saveAll(anyCollection());
        verify(roomRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("all")
    void testUpdateRoomType() {
        HotelPayload hotelPayload = mapToHotelPayload(hotelExpected, correlationId);
        HotelPayload.RoomTypePayload roomTypePayload = mapToRoomTypePayload(roomTypeExpected);
        roomTypePayload.setRoomTypePrice(new BigDecimal("90.00"));
        roomTypePayload.setHasBreakfast(true);
        hotelPayload.setRoomTypeList(Collections.singletonList(roomTypePayload));

        given(hotelRepository.checkIfHotelExists(anyString())).willReturn(1);

        hotelExpected.setActivated(oldTracking);
        given(hotelRepository.findActiveHotelByHotelId(anyString())).willReturn(Optional.of(hotelExpected));

        roomTypeExpected.setActivated(oldTracking);
        given(roomTypeRepository.findActiveRoomTypeByRoomTypeId(anyString())).willReturn(Optional.of(roomTypeExpected));

        consumerService.processMessage(hotelPayload);

        ArgumentCaptor<List<RoomType>> roomTypeArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(roomTypeRepository, times(1)).saveAll(roomTypeArgumentCaptor.capture());

        RoomType oldRoomTypeActual = roomTypeArgumentCaptor.getAllValues()
                .stream()
                .flatMap(Collection::stream)
                .filter(e -> e.getDeactivated() != null)
                .findAny()
                .orElse(new RoomType());

        RoomType newRoomTypeActual = roomTypeArgumentCaptor.getAllValues()
                .stream()
                .flatMap(Collection::stream)
                .filter(e -> e.getDeactivated() == null)
                .findAny()
                .orElse(new RoomType());

        assertThat(oldRoomTypeActual.getActivated()).isEqualTo(roomTypeExpected.getActivated()).isEqualTo(oldTracking);
        assertThat(newRoomTypeActual.getActivated()).isEqualTo(roomTypeExpected.getDeactivated()).isEqualTo(newTracking);
        assertThat(newRoomTypeActual.getRoomTypePrice()).isEqualTo(roomTypePayload.getRoomTypePrice());
        assertThat(newRoomTypeActual.isHasBreakfast()).isEqualTo(roomTypePayload.isHasBreakfast());
        assertThat(newRoomTypeActual.getDeactivated()).isNull();

        verify(hotelRepository, never()).saveAll(anyCollection());
        verify(hotelRepository, never()).save(any());
    }

    @Test
    void testDeactivateHotel() {
        HotelPayload hotelPayload = mapToHotelPayload(hotelExpected, correlationId);
        HotelPayload.RoomTypePayload roomTypePayload = mapToRoomTypePayload(roomTypeExpected);
        hotelPayload.setRoomTypeList(Collections.singletonList(roomTypePayload));
        hotelPayload.setAvailable(false);

        given(hotelRepository.checkIfHotelExists(anyString())).willReturn(1);

        hotelExpected.setActivated(oldTracking);
        given(hotelRepository.findActiveHotelByHotelId(anyString())).willReturn(Optional.of(hotelExpected));

        given(roomTypeRepository.findAllActiveRoomTypesByHotelId(anyString())).willReturn(Collections.singletonList(roomTypeExpected));
        given(roomRepository.findAllActiveRoomsByRoomTypeId(anyString())).willReturn(Collections.emptyList());

        consumerService.processMessage(hotelPayload);

        ArgumentCaptor<Hotel> hotelArgumentCaptor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository, times(1)).save(hotelArgumentCaptor.capture());

        ArgumentCaptor<RoomType> roomTypeArgumentCaptor = ArgumentCaptor.forClass(RoomType.class);
        verify(roomTypeRepository, times(1)).save(roomTypeArgumentCaptor.capture());

        assertThat(hotelArgumentCaptor.getValue().getDeactivated()).isEqualTo(newTracking);
        assertThat(roomTypeArgumentCaptor.getValue().getDeactivated()).isEqualTo(newTracking);
    }

    @Test
    void testReactivateHotel() {
        HotelPayload hotelPayload = mapToHotelPayload(hotelExpected, correlationId);
        HotelPayload.RoomTypePayload roomTypePayload = mapToRoomTypePayload(roomTypeExpected);
        hotelPayload.setRoomTypeList(Collections.singletonList(roomTypePayload));

        hotelExpected.setDeactivated(oldTracking);
        roomTypeExpected.setDeactivated(oldTracking);

        given(hotelRepository.checkIfHotelExists(anyString())).willReturn(1);
        given(hotelRepository.findActiveHotelByHotelId(anyString())).willReturn(Optional.empty());

        consumerService.processMessage(hotelPayload);

        ArgumentCaptor<Hotel> hotelArgumentCaptor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository, times(1)).save(hotelArgumentCaptor.capture());

        ArgumentCaptor<RoomType> roomTypeArgumentCaptor = ArgumentCaptor.forClass(RoomType.class);
        verify(roomTypeRepository, times(1)).save(roomTypeArgumentCaptor.capture());

        assertThat(hotelArgumentCaptor.getValue().getDeactivated()).isNull();
        assertThat(roomTypeArgumentCaptor.getValue().getDeactivated()).isNull();
    }

    @Test
    @Tag("skip")
    void testActivateHotel_tryDeactivateNonExistingHotel() {
        HotelPayload hotelPayload = mapToHotelPayload(hotelExpected, correlationId);
        hotelPayload.setRoomTypeList(Collections.emptyList());
        hotelPayload.setAvailable(false);

        assertThatThrownBy(() -> consumerService.processMessage(hotelPayload))
                .isInstanceOf(TransactionException.class)
                .hasMessage(ConsumerService.MSG_DEACTIVATE_NON_EXISTING_HOTEL);
    }
}