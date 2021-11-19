import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_service/auth.service';
import { TokenStorageService } from '../_service/token-storage.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit 
{
  form: any = {
    firstName: null,
    lastName: null,
    username: null,
    password: null
  };

  isSuccessful: boolean = false;
  isLoggedIn: boolean = false;
  isSignUpFailed: boolean = false;
  errorMessage: string = '';

  constructor(private authService : AuthService,
              private tokenService : TokenStorageService) {}

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenService.getToken();
  }

  onSubmit(): void {
    const { firstName, lastName, username, password } = this.form;

    this.authService.register(firstName, lastName, username, password).subscribe(
      (response: any) => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      (err: HttpErrorResponse) => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }

}
