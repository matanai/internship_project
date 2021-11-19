import { MessageRecord } from "./MessageRecord";

export class MessageReportPage 
{
    trackingId!: string;
    userNameAndRole!: string;
    dateTime!: Date;
    numMessages!: number;
    messageRecordList!: MessageRecord[];
}