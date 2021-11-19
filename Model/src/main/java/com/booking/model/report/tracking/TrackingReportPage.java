package com.booking.model.report.tracking;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingReportPage
{
    private List<TrackingRecord> trackingRecordList;
    private Integer totalRows;
    private Integer numOfPages;
    private Integer page;
    private Integer limit;
}
