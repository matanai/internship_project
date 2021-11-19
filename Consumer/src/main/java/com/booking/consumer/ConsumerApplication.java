package com.booking.consumer;

import com.booking.consumer.service.ConsumerService;
import com.booking.model.payload.HotelPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Basic Spring Boot application which listens for incoming messages from the dedicated message broker
 * @version 2.0.1
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.booking")
@EnableJpaRepositories(basePackages = { "com.booking.model" })
@EntityScan(basePackages = { "com.booking.model" })
public class ConsumerApplication
{
    private final ConsumerService consumerMessagingService;

    @Autowired
    public ConsumerApplication(ConsumerService consumerMessagingService) {
        this.consumerMessagingService = consumerMessagingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    /**
     * Listen for incoming messages
     */
    @RabbitListener(queues = "${amqp.queue}")
    public void listener(HotelPayload hotelPayloadList) {
        try {
            log.info("New message arrived");
            consumerMessagingService.processMessage(hotelPayloadList);
            log.info("Message successfully processed");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
