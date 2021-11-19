package com.booking.web.controller;

import com.booking.model.entity.Hotel;
import com.booking.model.entity.User;
import com.booking.model.payload.HotelPayload;
import com.booking.model.report.message.MessageReportPage;
import com.booking.model.report.tracking.TrackingReportPage;
import com.booking.web.service.ContentService;
import com.booking.web.service.MessageService;
import com.booking.web.service.ProducerService;
import com.booking.web.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/content")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContentRestController
{
    private final ReportService reportService;
    private final MessageService messageService;
    private final ContentService contentService;
    private final ProducerService producerService;

    @Autowired
    public ContentRestController(ReportService reportService, MessageService messageService,
                                 ContentService contentService, ProducerService producerService) {
        this.reportService = reportService;
        this.messageService = messageService;
        this.contentService = contentService;
        this.producerService = producerService;
    }

    @GetMapping("/report/{page}/{limit}")
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    public ResponseEntity<TrackingReportPage> getReport(@PathVariable("page") int page,
                                                        @PathVariable("limit") int limit) {
        return new ResponseEntity<>(reportService.prepareTrackingReport(page, limit), HttpStatus.OK);
    }

    @GetMapping("/message/{id}/{user}")
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    public ResponseEntity<MessageReportPage> getMessage(@PathVariable("id") String trackingId,
                                                        @PathVariable("user") String userNameAndRole) {
        return new ResponseEntity<>(messageService.prepareMessageReport(trackingId, userNameAndRole), HttpStatus.OK);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    public ResponseEntity<?> getFile(@RequestBody List<HotelPayload> hotelPayloadList,
                                     @AuthenticationPrincipal User user) {
        producerService.processMessage(user, hotelPayloadList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/hotels")
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    public ResponseEntity<List<Hotel>> getHotels() {
        return new ResponseEntity<>(contentService.prepareContent(), HttpStatus.OK);
    }
}
