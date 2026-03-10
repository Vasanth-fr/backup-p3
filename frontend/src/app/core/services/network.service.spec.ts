import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { NetworkService } from './network.service';
import { environment } from '../../../environments/environment';

describe('NetworkService', () => {

  let service: NetworkService;
  let httpMock: HttpTestingController;

  const API = `${environment.apiUrl}/network`;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(NetworkService);
    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get connections', () => {

    service.getConnections().subscribe();

    const req = httpMock.expectOne(`${API}/connections`);
    expect(req.request.method).toBe('GET');

  });

  it('should send connection request', () => {

    service.sendRequest(1).subscribe();

    const req = httpMock.expectOne(`${API}/connect/1`);
    expect(req.request.method).toBe('POST');

  });

  it('should follow user', () => {

    service.follow(1).subscribe();

    const req = httpMock.expectOne(`${API}/follow/1`);
    expect(req.request.method).toBe('POST');

  });

});
