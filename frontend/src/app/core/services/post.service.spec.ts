import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PostService } from './post.service';
import { environment } from '../../../environments/environment';

describe('PostService', () => {

  let service: PostService;
  let httpMock: HttpTestingController;

  const API = `${environment.apiUrl}/posts`;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create post', () => {

    service.createPost({} as any).subscribe();

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');

  });

  it('should like post', () => {

    service.likePost(1).subscribe();

    const req = httpMock.expectOne(`${API}/1/like`);
    expect(req.request.method).toBe('POST');

  });

});
