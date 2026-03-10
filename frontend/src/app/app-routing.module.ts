import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard, GuestGuard } from './core/guards/auth.guard';

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

const routes: Routes = [

  { path: '', redirectTo: 'feed', pathMatch: 'full' },

  // Auth
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [GuestGuard] },

  // Main
  { path: 'feed', component: FeedPageComponent, canActivate: [AuthGuard] },

  // Profile
  { path: 'profile/:id', component: ProfileViewComponent, canActivate: [AuthGuard] },
  { path: 'profile/:id/edit', component: ProfileEditComponent, canActivate: [AuthGuard] },

  // Single Post (used when clicking notification)
  { path: 'post/:id', component: FeedPageComponent, canActivate: [AuthGuard] },

  // Network
  { path: 'network', component: ConnectionsComponent, canActivate: [AuthGuard] },

  // Notifications
  { path: 'notifications', component: NotificationListComponent, canActivate: [AuthGuard] },

  // Fallback
  { path: '**', redirectTo: '/feed' }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
