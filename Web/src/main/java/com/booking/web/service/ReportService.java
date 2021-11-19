package com.booking.web.service;

import com.booking.model.entity.Message;
import com.booking.model.entity.Tracking;
import com.booking.model.entity.User;
import com.booking.model.report.tracking.TrackingRecord;
import com.booking.model.report.tracking.TrackingReportPage;
import com.booking.model.repository.TrackingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService
{
    @Value("${report.default.page}")
    private int defaultPage;

    @Value("${report.default.limit}")
    private int defaultLimit;

    private final TrackingRepository trackingRepository;

    @Autowired
    public ReportService(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    /**
     * Prepare report page. The method collects all retrieved data into a TrackingReportPage entity (a table of tracking
     * records to be shown on report page)
     */
    public TrackingReportPage prepareTrackingReport(Integer page, Integer limit) throws ServiceException {
        page = (page == null || page <= 0) ? defaultPage : page;
        limit = (limit == null || limit <= 0) ? defaultLimit : limit;

        int totalRows = (int) trackingRepository.count();
        int numOfPages = (totalRows <= limit) ? 0 : (int) Math.ceil(totalRows * 1.0 / limit);

        TrackingReportPage reportPage = new TrackingReportPage();
        reportPage.setTrackingRecordList(this.getTrackingRecordsList(page, limit));
        reportPage.setNumOfPages(numOfPages);
        reportPage.setTotalRows(totalRows);
        reportPage.setLimit(limit);
        reportPage.setPage(page);
        return reportPage;
    }

    /**
     * Prepare tracking records list. This method calculates offset (that is the record to begin with when displaying a
     * report page), downloads all tracking records from the database by offset and user defined limit (number of records
     * to display on the page), counts total number of tracking records currently present, and calculates the number of
     * pages to show in the pagination bar
     */
    public List<TrackingRecord> getTrackingRecordsList(int page, int limit) {
        return trackingRepository.getAllWithLimitAndOffset(limit, page * limit - limit)
                .stream()
                .map(tracking -> TrackingRecord.builder()
                        .successRate(this.calcSuccessRate(tracking))
                        .userNameAndRole(this.getFormattedUser(tracking.getUser()))
                        .correlationId(tracking.getCorrelationId())
                        .numMessages(tracking.getNumMessages())
                        .dateTime(tracking.getDateTime())
                        .trackingId(tracking.getId())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * Calculate percentage of successful messages from the total number of messages
     */
    private int calcSuccessRate(Tracking tracking) {
        double numSuccess = tracking.getMessages()
                .stream()
                .filter(Message::isSuccessful)
                .count();

        return (int) ((numSuccess / tracking.getNumMessages()) * 100);
    }

    /**
     * Get user first name, last name and roles as a single formatted string for message reports
     */
    private String getFormattedUser(User user) {
        String roles = user.getRoles()
                .stream()
                .map(e -> {
                    String role = e.getRoleName().replaceFirst("ROLE_", "").toLowerCase();
                    return Arrays.stream(role.split("_"))
                            .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                            .collect(Collectors.joining(" "));
                })
                .collect(Collectors.joining(", "));

        return user.getFirstName() + " " + user.getLastName() + ", " + roles;
    }
}
