import { RoomType } from "./RoomType";

export class Hotel 
{
    id!: number;
    hotelId!: string;
    hotelName!: string;
    hotelEmail!: string;
    hotelPhone!: string;
    hotelAddress!: string;
    hotelImgFile!: string;
    roomTypes!: RoomType[];
    isEnabled: boolean = true;
}