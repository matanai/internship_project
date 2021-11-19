import { Action } from "./Action";

export class MessageRecord
{
    hotelName!: string;
    isSuccessful!: boolean;
    dateTime!: Date;
    actionList!: Action[];
}