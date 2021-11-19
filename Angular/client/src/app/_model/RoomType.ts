import { Room } from "./Room";

export class RoomType 
{
    id!: number;
    roomTypeId!: string;
    hotelId!: string;
    roomTypeName!: string;
    roomTypePrice!: number;
    hasBreakfast!: boolean;
    hasRefund!: boolean;
    maxGuests!: number;
    roomTypeImgFile!: string;
    rooms!: Room[];
    isEnabled: boolean = true;
}