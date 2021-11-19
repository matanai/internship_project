import {TrackingRecord} from "./TrackingRecord";

export interface TrackingReportPage
{
    trackingRecordList : TrackingRecord[];
    totalRows : number;
    numOfPages : number;
    page : number;
    limit : number;
}