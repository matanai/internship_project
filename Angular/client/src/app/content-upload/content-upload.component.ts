import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { ContentService } from '../_service/content.service';
import { TokenStorageService } from '../_service/token-storage.service';

@Component({
  selector: 'app-content-upload',
  templateUrl: './content-upload.component.html',
  styleUrls: ['./content-upload.component.css']
})
export class ContentUploadComponent implements OnInit 
{
  public authorized: boolean = false;
  public message: string = environment.msgMustBeContent;

  constructor(private contentService: ContentService,
              private tokenService: TokenStorageService,
              private router: Router) {}
  
  ngOnInit(): void {
    if (!!this.tokenService.getToken()) {
      if (this.tokenService.getUser().roles.includes('ROLE_CONTENT_MANAGER')) {
        this.authorized = true;
      }
    }
  }

  onUploadFile(event: any): void {
    if (event.target.files.length !== 1) {
      console.error('No file selected');
    } else {
      const reader : FileReader | any = new FileReader();
      reader.onloadend = (e: string) => {
        this.contentService.sendHotelPayload(reader.result).subscribe(
          () => {},
          (error : HttpErrorResponse) => {
            console.error(error.message);
          }
        );
        this.router.navigate(['content/report']).then(() => {
          window.location.reload();
        });
      };
      reader.readAsText(event.target.files[0]);
    }
  }
}
