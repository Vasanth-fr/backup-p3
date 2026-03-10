import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  unreadCount = 0;
  currentUserId: number | null;
  username: string | null;

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService
  ) {
    this.currentUserId = authService.getCurrentUserId();
    this.username = authService.getCurrentUsername();
  }

  ngOnInit() {
    this.notificationService.unreadCount$.subscribe((count: number) => {
      this.unreadCount = count;
    });
    this.notificationService.getUnreadCount().subscribe();
  }

  logout() {
    this.authService.logout();
  }
}
