import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../shared/models/models';
import { Router } from '@angular/router';
import { interval } from 'rxjs';

@Component({
  selector: 'app-notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.css']
})
export class NotificationListComponent implements OnInit {

  notifications: Notification[] = [];
  loading: boolean = true;

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
   this.loadNotifications();

   // Auto refresh every 10 sec
   interval(10000).subscribe(() => {
     this.loadNotifications();
   });
  }

  // ================= LOAD =================
  loadNotifications(): void {
    this.loading = true;
    // P3: returns Notification[] directly (no wrapper)
    this.notificationService.getNotifications()
      .subscribe({
        next: notifications => {
          this.notifications = notifications;
          this.loading = false;
        },
        error: () => {
          this.notifications = [];
          this.loading = false;
        }
      });
  }

  // ================= MARK ONE =================
  markRead(notification: Notification): void {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe(() => {
        notification.read = true;
      });
    }

    if (
      notification.type === 'POST_LIKED' ||
      notification.type === 'POST_COMMENTED' ||
      notification.type === 'POST_SHARED'
    ) {
      if (notification.referenceId) {
        this.router.navigate(['/post', notification.referenceId])
          .then(() => window.scrollTo(0, 0));
      }
    }
  }

  // ================= MARK ALL =================
  markAllRead(): void {
    this.notificationService.markAllAsRead()
      .subscribe(() => {
        this.notifications.forEach(n => n.read = true);
      });
  }

  // ================= DELETE =================
  deleteNotif(notification: Notification, index: number, event: Event): void {
    event.stopPropagation();
    this.notificationService.deleteNotification(notification.id).subscribe();
    this.notifications.splice(index, 1);
  }

  // ================= UNREAD COUNT =================
  getUnreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  // ================= ICON =================
  getIcon(type: string): string {
    switch (type) {
      case 'NEW_FOLLOWER': return '👤';
      case 'POST_LIKED': return '❤️';
      case 'POST_COMMENTED': return '💬';
      case 'CONNECTION_REQUEST': return '🔗';
      default: return '🔔';
    }
  }

  // ================= ICON BACKGROUND =================
  getIconBg(type: string): string {
    switch (type) {
      case 'NEW_FOLLOWER': return 'bg-primary';
      case 'POST_LIKED': return 'bg-danger';
      case 'POST_COMMENTED': return 'bg-success';
      case 'CONNECTION_REQUEST': return 'bg-warning';
      default: return 'bg-secondary';
    }
  }

  // ================= MESSAGE FORMAT =================
  getMessage(notification: Notification): string {
    return notification.message || 'You have a new notification';
  }
}
