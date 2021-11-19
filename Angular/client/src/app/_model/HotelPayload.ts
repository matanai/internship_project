import { RoomTypePayload } from "./RoomTypePayload";

export class HotelPayload 
{
    correlationId!: string;
    available!: boolean;
    hotelId!: string;
    hotelName!: string;
    hotelEmail!: string;
    hotelPhone!: string;
    hotelAddress!: string;
    hotelImgFile!: string;
    roomTypeList!: RoomTypePayload[]; 
}