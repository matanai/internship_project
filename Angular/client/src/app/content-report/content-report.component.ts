import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { TrackingReportPage } from '../_model/TrackingReportPage';
import { ContentService } from '../_service/content.service';

@Component({
  selector: 'app-content-report',
  templateUrl: './content-report.component.html',
  styleUrls: ['./content-report.component.css']
})
export class ContentReportComponent implements OnInit 
{
  public trackingReportPage?: TrackingReportPage | any;
  public authorized: boolean = true;
  public message: string = environment.msgMustBeContent;

  constructor(private contentService: ContentService) { }

  ngOnInit(): void {
    this.getReport();
  }

  getReport(page: number = 0, limit: number = 0): void {
    this.contentService.getReport(page, limit).subscribe(
      (response: TrackingReportPage) => {
        this.trackingReportPage = response;
        this.authorized = true;
      },
      (error: HttpErrorResponse) => {
        this.authorized = false;
      }
    );
  }

  showPrev(page: number): string {
    return page != 1 ? 'page-item' : 'page-item disabled';
  }

  showNext(page: number, numOfPages: number): string {
    return page < numOfPages ? 'page-item' : 'page-item disabled';
  }

  getPageCounter(totalPages: number): number[] {
    let array : number[] = [];
    for (let i = 0; i <= totalPages; i++) {
        array[i] = i;
    }
    return array;
  }

  getSuccessRateColor(successRate: number): string {
    return (successRate >= 75) ? 'text-success' : (successRate <= 25 ? 'text-danger' : 'text-warning');
  }

  getDateTimeFormat(dateTime: Date): string {
    return new Date(dateTime).toLocaleString();
  }
}
