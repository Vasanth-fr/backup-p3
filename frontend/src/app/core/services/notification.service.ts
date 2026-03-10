import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, interval, Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Notification } from '../../shared/models/models';

// P3 NotificationCountResponse shape
export interface NotificationCountResponse {
  total: number;
  unread: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private readonly API = `${environment.apiUrl}/notifications`;
  private unreadCount = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCount.asObservable();

  constructor(private http: HttpClient) {}

  // P3: returns List<NotificationResponse> directly (no PageResponse wrapper)
  getNotifications(): Observable<Notification[]> {
    return this.http.get<any[]>(this.API).pipe(
      map(notifications => notifications.map(notification => ({
        ...notification,
        read: notification.read ?? notification.isRead ?? false
      })))
    );
  }

  // P3: GET /api/notifications/count  (was /unread-count in P2)
  getUnreadCount(): Observable<NotificationCountResponse> {
    return this.http.get<NotificationCountResponse>(`${this.API}/count`).pipe(
      tap(res => this.unreadCount.next(res.unread ?? 0))
    );
  }

  // P3: PUT /api/notifications/{id}/read
  markAsRead(id: number): Observable<Notification> {
    return this.http.put<any>(`${this.API}/${id}/read`, {}).pipe(
      map(notification => ({
        ...notification,
        read: notification.read ?? notification.isRead ?? true
      }))
    );
  }

  // P3: PUT /api/notifications/read-all
  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.API}/read-all`, {});
  }

  // P3: DELETE /api/notifications/{id}
  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  // Poll for new notifications every 30 seconds
  startPolling(): void {
    interval(30000).pipe(
      switchMap(() => this.getUnreadCount())
    ).subscribe();
  }
}
