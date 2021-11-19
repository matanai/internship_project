import { Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { TokenStorageService } from './_service/token-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent 
{
  readonly baseUrl: string = environment.baseUrl;
  readonly exportPage: string = environment.baseUrl + environment.adminExport;

  roles: string[] = [];
  username?: string;
  isLoggedIn: boolean = false;

  // Menu links
  showAdminUsers: boolean = false;
  showAdminExport: boolean = false;
  showContentUpload: boolean = false;
  showContentReport: boolean = false;
  showContentHotels: boolean = false;

  constructor(private tokenService: TokenStorageService) {}

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenService.getUser();
      this.username = user.username.substring(0, user.username.indexOf('@'));
      this.roles = user.roles;

      if (this.roles.includes('ROLE_ADMIN')) {
        this.showAdminUsers = true;
        this.showAdminExport = true;
      }

      if (this.roles.includes('ROLE_CONTENT_MANAGER')) {
        this.showContentUpload = true;
        this.showContentReport = true;
        this.showContentHotels = true;
      }
    }
  }

  logout(): void {
    this.tokenService.signOut();
    window.location.reload();
  }

  getRoleNamePretty(role: string): string {  
    return role.substring(5);
  }
}
