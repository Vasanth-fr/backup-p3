import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { UserService } from './user.service';
import { environment } from '../../../environments/environment';

describe('UserService', () => {

  let service: UserService;
  let httpMock: HttpTestingController;

  const API = `${environment.apiUrl}/users`;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get user by id', () => {

    service.getUserById(1).subscribe();

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('GET');

  });

  it('should update profile', () => {

    service.updateProfile({}).subscribe();

    const req = httpMock.expectOne(`${API}/profile`);
    expect(req.request.method).toBe('PUT');

  });

  it('should search users from the users endpoint', () => {

    service.searchUsers('alex').subscribe(users => {
      expect(users.length).toBe(1);
      expect(users[0].email).toBe('alex@example.com');
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('GET');

    req.flush([
      { id: 1, email: 'alex@example.com', firstName: 'Alex' },
      { id: 2, email: 'sam@example.com', firstName: 'Sam' }
    ]);

  });

});
