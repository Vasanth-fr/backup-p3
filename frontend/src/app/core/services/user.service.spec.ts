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

    service.updateProfile(1, {}).subscribe();

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('PUT');

  });

});
