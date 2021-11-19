import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from 'src/environments/environment';
import { MessageReportPage } from '../_model/MessageReportPage';
import { ContentService } from '../_service/content.service';

@Component({
  selector: 'app-content-message',
  templateUrl: './content-message.component.html',
  styleUrls: ['./content-message.component.css']
})
export class ContentMessageComponent implements OnInit 
{
  public messageReportPage?: MessageReportPage | any;

  readonly add: string = environment.actionColorAdd;
  readonly update: string = environment.actionColorUpdate;
  readonly remove: string = environment.actionColorRemove;

  constructor(private contentService: ContentService,
              private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
        this.getMessage(params.get('id'), params.get('user'));
      }
    );
  }

  getMessage(id: string | any, user: string | any): void {
    this.contentService.getMessage(id, user).subscribe(
      (response: MessageReportPage) => {
        this.messageReportPage = response;
    },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  getActionColor(action: string): string {
    return action === 'ADD' ? this.add : (action === 'UPDATE' ? this.update : this.remove);
  }

  getDateTimeFormat(dateTime: Date): string {
    return new Date(dateTime).toLocaleString();
  }

}
