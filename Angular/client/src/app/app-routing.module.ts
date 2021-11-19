import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminUsersComponent } from './admin-users/admin-users.component';
import { ContentHotelsComponent } from './content-hotels/content-hotels.component';
import { ContentMessageComponent } from './content-message/content-message.component';
import { ContentReportComponent } from './content-report/content-report.component';
import { ContentUploadComponent } from './content-upload/content-upload.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { RegisterComponent } from './register/register.component';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'admin', redirectTo: 'admin/users', pathMatch: 'full' }, 
  { path: 'admin/users', component: AdminUsersComponent },
  { path: 'content', redirectTo: 'content/report', pathMatch: 'full' },
  { path: 'content/report', component: ContentReportComponent },
  { path: 'content/upload', component: ContentUploadComponent },
  { path: 'content/hotels', component: ContentHotelsComponent },
  { path: 'content/message/:id/:user', component: ContentMessageComponent },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
