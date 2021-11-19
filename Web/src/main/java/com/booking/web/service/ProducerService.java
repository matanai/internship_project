package com.booking.web.service;

import com.booking.model.entity.Tracking;
import com.booking.model.entity.User;
import com.booking.model.payload.HotelPayload;
import com.booking.model.repository.TrackingRepository;
import com.booking.model.util.UUIDGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for processing JSON and sending it to the message broker. We generate unique tracking id and correlation
 * id which is used to relate each message to a particular tracking on the consumer side. Then we parse input stream with
 * JSON data into a list of strings and map each element to a {@link HotelPayload} object. If successful we create new
 * record in the tracking table and send the list of {@link HotelPayload} objects to the message broker. Since there
 * maybe more the one async calls for processing all operations are conducted in a separate thread
 *
 * @version 2.0.1
 */
@Slf4j
@Service
public class ProducerService
{
    private final TrackingRepository trackingRepository;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public ProducerService(TrackingRepository trackingRepository, AmqpTemplate amqpTemplate) {
        this.trackingRepository = trackingRepository;
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * Service class operation starts here
     */
    @Async("threadPoolTaskExecutor")
    public void processMessage(User user, List<HotelPayload> hotelPayloadList) {
        log.info("New hotel payload list arrived");

        final UUID trackingId = UUIDGeneratorUtil.getUUID();
        final UUID correlationId = UUIDGeneratorUtil.getUUID();

        try {
            for (HotelPayload payload : hotelPayloadList) {
                payload.setCorrelationId(correlationId);
                this.validate(payload);
            }

            this.createNewTrackingRecord(trackingId, correlationId, user, hotelPayloadList.size());

            if (trackingRepository.checkCorrelationId(correlationId) != 1) {
                throw new ServiceException("Message processing aborted: tracking record was not persisted");
            }

            ((RabbitTemplate) amqpTemplate).setCorrelationKey(correlationId.toString());
            hotelPayloadList.forEach(amqpTemplate::convertAndSend);

            log.info("Data successfully parsed, mapped, and sent to messaging broker ({} messages)", hotelPayloadList.size());


        } catch (ServiceException e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * Create new tracking record
     */
    private void createNewTrackingRecord(UUID trackingId, UUID correlationId, User user, int numMessages) {
        Tracking tracking = Tracking.builder()
                .dateTime(LocalDateTime.now())
                .correlationId(correlationId)
                .numMessages(numMessages)
                .id(trackingId)
                .user(user)
                .build();

        trackingRepository.save(tracking);
        log.info("Tracking record successfully created");
    }

    /**
     * Conduct basic validation of the payload immediately after parsing
     */
    private void validate(HotelPayload hotelPayload) throws ServiceException {
        if (hotelPayload.getHotelId() == null || hotelPayload.getHotelId().isEmpty()) {
            throw new ServiceException("Validation failed: hotel id is null or empty");
        }

        if (hotelPayload.getRoomTypeList() != null && !hotelPayload.getRoomTypeList().isEmpty()) {
            for (HotelPayload.RoomTypePayload roomTypePayload : hotelPayload.getRoomTypeList()) {
                if (roomTypePayload.getRoomTypeId() == null || roomTypePayload.getRoomTypeId().isEmpty()) {
                    throw new ServiceException("Validation failed: room type id is null or empty");
                }
            }
        }

        log.info("HotelPayload validated");
    }
}
