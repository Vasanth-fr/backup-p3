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

    const req = httpMock.expectOne(`${API}?page=0&size=20`);
    expect(req.request.method).toBe('GET');

  });

  it('should mark notification as read', () => {

    service.markAsRead(1).subscribe();

    const req = httpMock.expectOne(`${API}/1/read`);
    expect(req.request.method).toBe('PUT');

  });

});
