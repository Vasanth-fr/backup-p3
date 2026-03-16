import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  unreadCount = 0;
  currentUserId: number | null = null;
  displayName: string | null = null;
  isLoggedIn = false;

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    // Reactively update when user logs in/out
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.currentUserId = user?.userId ?? null;
      this.displayName = user?.email?.split('@')[0] ?? null;
    });

    this.notificationService.unreadCount$.subscribe((count: number) => {
      this.unreadCount = count;
    });

    if (this.authService.isLoggedIn()) {
      this.notificationService.getUnreadCount().pipe(
        catchError(() => EMPTY)
      ).subscribe();
    }
  }

  logout() {
    this.authService.logout();
  }
}
