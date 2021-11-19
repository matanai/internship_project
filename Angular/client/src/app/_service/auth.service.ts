import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';

const LOGIN_URL = environment.baseUrl + environment.login;
const REGISTER_URL = environment.baseUrl + environment.register;

const HTTP_OPTIONS = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService 
{
  constructor(private http: HttpClient) {}

  login(username: string, password: string) : Observable<any> {
    return this.http.post(LOGIN_URL, {
      username,
      password
    }, HTTP_OPTIONS);
  }

  register(firstName: string, lastName: string, username: string, password: string): Observable<any> {
    return this.http.post(REGISTER_URL, {
      firstName, 
      lastName,
      username,
      password
    }, HTTP_OPTIONS);
  }
}
