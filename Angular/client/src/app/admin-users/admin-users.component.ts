import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Role } from '../_model/Role';
import { User } from '../_model/User';
import { AdminService } from '../_service/admin.service';
import { TokenStorageService } from '../_service/token-storage.service';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit 
{
  public users!: User[];
  public currentUser!: User; 
  public editUser!: User;

  public authorized: boolean = true;
  readonly message: string = environment.msgMustBeAdmin;

  constructor(private adminService: AdminService,
              private tokenService: TokenStorageService) {}

  ngOnInit(): void {
    this.getUsers();
    this.currentUser = this.tokenService.getUser();
  }

  getUsers(): void {
    this.adminService.getUsers().subscribe(
      (response: any) => {
        this.users = response;
        this.authorized = true;
      },
      (error: HttpErrorResponse) => {
        this.authorized = false;
      }
    );
  }

  onOpenEditUserModal(user: User): void {
    this.editUser = user;
  }

  onEditUser(formUser: any): void {
    this.editUser!.firstName = formUser.firstName;
    this.editUser!.lastName = formUser.lastName;
    this.editUser!.enabled = formUser.enabled;
    this.editUser!.authorities = null;
    this.updateRoles(formUser);

    this.adminService.updateUser(this.editUser).subscribe(
      () => {
        // Small delay to give server time to update
        setTimeout(() => this.getUsers(), 100);
      }, 
      (error : HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  updateRoles(formUser: any): void {
    let newRoles : Role[] = [];

    if (formUser.roleAdmin) {
      newRoles.push(new Role(1, 'ROLE_ADMIN', 'ROLE_ADMIN'));
    }

    if (formUser.roleContentManager) {
      newRoles.push(new Role(2, 'ROLE_CONTENT_MANAGER', 'ROLE_CONTENT_MANAGER'));
    }

    if (formUser.roleSalesManager) {
      newRoles.push(new Role(3, 'ROLE_SALES_MANAGER', 'ROLE_SALES_MANAGER'));
    }

    if (formUser.roleUser) {
      newRoles.push(new Role(4, 'ROLE_USER', 'ROLE_USER'));
    }

    this.editUser!.roles = newRoles;

    // User must have at least ROLE_USER
    if (!this.editUser!.roles.length) {
      this.editUser!.roles.push(new Role(4, 'ROLE_USER', 'ROLE_USER'));
    }
  }

  hasRole(roles: Role[] | any, roleName: string): boolean {
    if (roles != undefined) {
      for (let role of roles) {
        if (role.roleName === roleName) {
          return true;
        }
      }
    }
    
    return false;
  }

  getRoleNamePretty(role: Role): string {
    return role.roleName.substring(5);
  }

  getStylesIfCurrentUser(user: User): string {
    return (user?.username == this.currentUser?.username) ? 'currentuser' : '';
  }
}
