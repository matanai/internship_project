import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_service/auth.service';
import { TokenStorageService } from '../_service/token-storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit 
{
  form: any = {
    username: null,
    password: null
  };
 
  isLoggedIn: boolean = false;
  isLoginFailed: boolean = false;
  errorMessage: string = '';
  roles: string[] = [];

  constructor(private authService: AuthService, 
              private tokenService: TokenStorageService) {}

  ngOnInit(): void {
    if (this.tokenService.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenService.getUser().roles;
    
    }
  }

  onSubmit(): void {
    const { username, password } = this.form;

    this.authService.login(username, password).subscribe(
      (response: any) => {
        this.tokenService.saveToken(response.token);
        this.tokenService.saveUser(response);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenService.getUser().roles;
        this.reloadPage();        
      },
      (err: HttpErrorResponse) => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    );
  }

  getRoleNamePretty(role: string): string {  
    return role.substring(5);
  }

  reloadPage(): void {
    window.location.reload();
  }

}
