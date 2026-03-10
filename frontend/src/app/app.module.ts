import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TokenInterceptor } from './core/interceptors/token.interceptor';

// Auth
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';

// Feed
import { FeedPageComponent } from './features/feed-page/feed-page.component';

// Users
import { ProfileViewComponent } from './features/users/profile-view/profile-view.component';
import { ProfileEditComponent } from './features/users/profile-edit/profile-edit.component';

// Network
import { ConnectionsComponent } from './features/network/connections/connections.component';

// Notifications
import { NotificationListComponent } from './features/notifications/notification-list/notification-list.component';

// Shared
import { NavbarComponent } from './shared/components/navbar/navbar.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    FeedPageComponent,
    ProfileViewComponent,
    ProfileEditComponent,
    ConnectionsComponent,
    NotificationListComponent,
    NavbarComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    RouterModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}