import {HttpErrorResponse} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {environment} from 'src/environments/environment';
import {Hotel} from '../_model/Hotel';
import {HotelPayload} from '../_model/HotelPayload';
import {Room} from '../_model/Room';
import {RoomType} from '../_model/RoomType';
import {RoomTypePayload} from '../_model/RoomTypePayload';
import {ContentService} from '../_service/content.service';

@Component({
  selector: 'app-content-hotels',
  templateUrl: './content-hotels.component.html',
  styleUrls: ['./content-hotels.component.css']
})
export class ContentHotelsComponent implements OnInit 
{
  public hotels: Hotel[] | any = [];
  public editHotel: Hotel | any;
  public editRoomType: RoomType | any;
  public roomsToDeactivate: Room[] | any = [];

  public baseUrl: string = environment.baseUrl;

  public authorized: boolean = true;
  readonly message: string = environment.msgMustBeContent;
  
  constructor(private contentService : ContentService) { }

  ngOnInit(): void {
    this.getHotels();
  }

  getHotels(): void {
      this.contentService.getHotels().subscribe(
        (response: Hotel[]) => {
          this.hotels = response;
          this.authorized = true;
        },
        (error: HttpErrorResponse) => {
          this.authorized = false;
        }
      );
  }

  sendHotelPayload(hotelPayload: HotelPayload): void {
    let stringList = JSON.stringify([ hotelPayload ]);
    this.contentService.sendHotelPayload(stringList).subscribe(
      () => {
        // Small delay to give server time to refresh
        setTimeout(() => this.getHotels(), 100);
      }, 
      (error : HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }
  
  onEditHotel(formHotel: Hotel): void {
    let hotelPayload = this.prepareHotelPayload(formHotel);
    this.sendHotelPayload(hotelPayload);
  }

  onEditRoomType(formRoomType: RoomType): void {
    this.editHotel.isEnabled = true;
    let hotelPayload = this.prepareHotelPayload(this.editHotel, formRoomType);
    this.sendHotelPayload(hotelPayload);
  }
  
  onOpenEditHotelModal(hotel: Hotel): void {
    this.editHotel = hotel;
  }

  onOpenEditRoomTypeModal(roomType: RoomType, hotel: Hotel): void {
    this.editRoomType = roomType;
    this.editHotel = hotel;
  }

  onDeactivateRoom(room: Room, isChecked : any): void {
    if (!isChecked.target.checked) {
      this.roomsToDeactivate.push(room);
    } else {
      this.roomsToDeactivate.pop(room);
    }
  }

  onOpenAddRoomTypeModal(hotel: Hotel): void {
    this.editHotel = hotel;
  }

  onAddRoomType(formRoomType: NgForm): void {
    let roomType = new RoomType;
    roomType.isEnabled = true;
    roomType.roomTypeId = formRoomType.value.roomTypeId;
    roomType.roomTypeName = formRoomType.value.roomTypeName;
    roomType.roomTypePrice = formRoomType.value.roomTypePrice;
    roomType.roomTypeImgFile = formRoomType.value.roomTypeImgFile;
    roomType.maxGuests = formRoomType.value.maxGuests;
    roomType.hasBreakfast = formRoomType.value.hasBreakfast;
    roomType.hasRefund = formRoomType.value.hasRefund;

    this.editHotel.isEnabled = true;
    let hotelPayload = this.prepareHotelPayload(this.editHotel, roomType);
    this.sendHotelPayload(hotelPayload);
    formRoomType.reset();
  }

  prepareHotelPayload(formHotel: Hotel, formRoomType?: RoomType): HotelPayload {
    let hotelPayload = new HotelPayload();
    hotelPayload.available = formHotel.isEnabled;
    hotelPayload.hotelId = formHotel.hotelId;
    hotelPayload.hotelName = formHotel.hotelName;
    hotelPayload.hotelEmail = formHotel.hotelEmail;
    hotelPayload.hotelPhone = formHotel.hotelPhone;
    hotelPayload.hotelAddress = formHotel.hotelAddress;
    hotelPayload.hotelImgFile = formHotel.hotelImgFile;
    hotelPayload.roomTypeList = [];
    
    if (formRoomType != null) {
      let roomTypePayload = new RoomTypePayload();
      roomTypePayload.available = formRoomType.isEnabled;
      roomTypePayload.roomTypeId = formRoomType.roomTypeId;
      roomTypePayload.roomTypeName = formRoomType.roomTypeName;
      roomTypePayload.roomTypePrice = formRoomType.roomTypePrice;
      roomTypePayload.maxGuests = formRoomType.maxGuests;
      roomTypePayload.hasBreakfast = formRoomType.hasBreakfast;
      roomTypePayload.hasRefund = formRoomType.hasRefund;
      roomTypePayload.roomTypeImgFile = formRoomType.roomTypeImgFile;
      roomTypePayload.roomIdList = [];

      if (this.roomsToDeactivate.length) {
        roomTypePayload.available = false;
        for (let room of this.roomsToDeactivate) {
          roomTypePayload.roomIdList.push(room.roomId);
        }
        this.roomsToDeactivate = [];
      }

      hotelPayload.roomTypeList = [roomTypePayload];
    }

    return hotelPayload;
  }

}
