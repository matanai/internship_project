import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../_model/User';

@Injectable({
  providedIn: 'root'
})
export class AdminService 
{
  private adminUsersPage: string = environment.baseUrl + environment.adminUsers;
  private adminUserUpdatePage: string = environment.baseUrl + environment.adminUserUpdate;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.adminUsersPage}`);
  }

  updateUser(user: User | any) : Observable<any> {
    return this.http.put<any>(`${this.adminUserUpdatePage}`, user);
  } 
}
