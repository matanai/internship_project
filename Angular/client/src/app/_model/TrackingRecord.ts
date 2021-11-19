export class TrackingRecord
{
    trackingId!: string;
    correlationId!: string;
    userNameAndRole!: string;
    numMessages!: number;
    successRate!: number;
    dateTime!: Date;
}