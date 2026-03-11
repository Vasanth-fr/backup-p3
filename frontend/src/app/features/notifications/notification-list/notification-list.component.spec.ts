import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { NotificationListComponent } from './notification-list.component';
import { NotificationService } from '../../../core/services/notification.service';

describe('NotificationListComponent', () => {
  let component: NotificationListComponent;
  let fixture: ComponentFixture<NotificationListComponent>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'getNotifications',
      'markAsRead',
      'markAllAsRead',
      'deleteNotification'
    ]);

    notificationServiceSpy.getNotifications.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [NotificationListComponent],
      providers: [{ provide: NotificationService, useValue: notificationServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load notifications on init', () => {
    expect(notificationServiceSpy.getNotifications).toHaveBeenCalled();
  });

  it('should return unread count', () => {
    component.notifications = [
      { id: 1, read: false } as any,
      { id: 2, read: true } as any
    ];

    expect(component.getUnreadCount()).toBe(1);
  });

  it('should mark notification as read', () => {
    notificationServiceSpy.markAsRead.and.returnValue(of({
      id: 1,
      recipientId: 1,
      type: 'CONNECTION_REQUEST',
      message: 'Connection request',
      read: true,
      createdAt: new Date().toISOString()
    }));

    const notif: any = { id: 1, read: false, type: 'CONNECTION_REQUEST', message: 'Connection request' };
    component.markRead(notif);

    expect(notificationServiceSpy.markAsRead).toHaveBeenCalledWith(1);
    expect(notif.read).toBeTrue();
  });
});
