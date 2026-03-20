import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { NotificationService } from './notification.service';
import { environment } from '../../../environments/environment';

describe('NotificationService', () => {

  let service: NotificationService;
  let httpMock: HttpTestingController;

  const API = `${environment.apiUrl}/notifications`;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch notifications', () => {

    service.getNotifications().subscribe();

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('GET');

  });

  it('should mark notification as read', () => {

    service.markAsRead(1).subscribe();

    const req = httpMock.expectOne(`${API}/1/read`);
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 1, isRead: true });

  });

  it('should update unread count immediately when marking notification as read', () => {
    const counts: number[] = [];
    service.unreadCount$.subscribe(count => counts.push(count));

    service.getUnreadCount().subscribe();
    const countReq = httpMock.expectOne(`${API}/count`);
    countReq.flush({ total: 3, unread: 2 });

    service.markAsRead(1).subscribe();
    const readReq = httpMock.expectOne(`${API}/1/read`);
    readReq.flush({ id: 1, isRead: true });

    expect(counts.at(-1)).toBe(1);
  });

  it('should reset unread count immediately when marking all as read', () => {
    const counts: number[] = [];
    service.unreadCount$.subscribe(count => counts.push(count));

    service.getUnreadCount().subscribe();
    const countReq = httpMock.expectOne(`${API}/count`);
    countReq.flush({ total: 5, unread: 4 });

    service.markAllAsRead().subscribe();
    const readAllReq = httpMock.expectOne(`${API}/read-all`);
    readAllReq.flush({});

    expect(counts.at(-1)).toBe(0);
  });

});
