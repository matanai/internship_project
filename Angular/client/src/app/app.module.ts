import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AdminUsersComponent } from './admin-users/admin-users.component';
import { ContentUploadComponent } from './content-upload/content-upload.component';
import { ContentReportComponent } from './content-report/content-report.component';
import { ContentMessageComponent } from './content-message/content-message.component';
import { ContentHotelsComponent } from './content-hotels/content-hotels.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { authInterceptorProviders } from './_helpers/auth.interceptor';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AdminUsersComponent,
    ContentUploadComponent,
    ContentReportComponent,
    ContentMessageComponent,
    ContentHotelsComponent,
    LoginComponent,
    RegisterComponent,
    NotFoundComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [authInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
