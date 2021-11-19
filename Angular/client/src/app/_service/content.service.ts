import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Hotel } from '../_model/Hotel';
import { MessageReportPage } from '../_model/MessageReportPage';
import { TrackingReportPage } from '../_model/TrackingReportPage';

@Injectable({
  providedIn: 'root'
})
export class ContentService 
{
  private contentReportPage : string = environment.baseUrl + environment.contentReport;
  private contentMessagePage : string = environment.baseUrl + environment.contentMessage;
  private contentUploadPage : string = environment.baseUrl + environment.contentUpload;
  private contentHotelsPage : string = environment.baseUrl + environment.contentHotels; 

  constructor(private http: HttpClient) {}

  getReport(page: number, limit: number): Observable<TrackingReportPage> {
    return this.http.get<TrackingReportPage>(`${this.contentReportPage}/${page}/${limit}`);
  }

  getMessage(id: string, user: string): Observable<MessageReportPage> {
    return this.http.get<MessageReportPage>(`${this.contentMessagePage}/${id}/${user}`);
  }

  sendHotelPayload(hotelPayloadList: string): Observable<any> {
    const options = { headers : new HttpHeaders({ 'Content-Type': 'application/json' }) };
    return this.http.post<any>(`${this.contentUploadPage}`, hotelPayloadList, options);
  }

  getHotels(): Observable<Hotel[]> {
    return this.http.get<Hotel[]>(`${this.contentHotelsPage}`);
  }
}
