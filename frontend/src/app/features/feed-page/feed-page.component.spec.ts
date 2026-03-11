import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FeedPageComponent } from './feed-page.component';
import { PostService } from '../../core/services/post.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { NetworkService } from '../../core/services/network.service';

describe('FeedPageComponent', () => {
  let component: FeedPageComponent;
  let fixture: ComponentFixture<FeedPageComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let networkServiceSpy: jasmine.SpyObj<NetworkService>;

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj('PostService', [
      'getFeed',
      'createPost',
      'likePost',
      'unlikePost',
      'addComment'
    ]);
    userServiceSpy = jasmine.createSpyObj('UserService', ['searchUsers']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUserId']);
    networkServiceSpy = jasmine.createSpyObj('NetworkService', ['follow']);

    postServiceSpy.getFeed.and.returnValue(of({
      posts: [],
      totalElements: 0,
      totalPages: 1,
      currentPage: 0
    }));
    userServiceSpy.searchUsers.and.returnValue(of([]));
    authServiceSpy.getCurrentUserId.and.returnValue(1);

    await TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule],
      declarations: [FeedPageComponent],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: NetworkService, useValue: networkServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FeedPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load feed on init', () => {
    expect(postServiceSpy.getFeed).toHaveBeenCalledWith(0, 10);
  });

  it('should search users', fakeAsync(() => {
    userServiceSpy.searchUsers.and.returnValue(of([{ id: 2, email: 'john@example.com', username: 'john' }]));

    component.onSearchInput('john');
    tick(500);

    expect(userServiceSpy.searchUsers).toHaveBeenCalledWith('john');
    expect(component.users.length).toBe(1);
  }));

  it('should create post', () => {
    component.newPostContent = 'Test Post';
    postServiceSpy.createPost.and.returnValue(of({
      id: 1,
      author: {} as any,
      content: 'Test Post',
      type: 'TEXT',
      pinned: false,
      viewCount: 0,
      likeCount: 0,
      commentCount: 0,
      shareCount: 0,
      likedByCurrentUser: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }));

    component.createPost();

    expect(postServiceSpy.createPost).toHaveBeenCalled();
    expect(component.posts.length).toBe(1);
  });

  it('should add comment', () => {
    const post: any = { id: 1, comments: [], commentCount: 0, newComment: 'Nice post' };
    postServiceSpy.addComment.and.returnValue(of({
      id: 1,
      content: 'Nice post',
      username: 'john',
      userId: 1,
      postId: 1,
      likeCount: 0,
      likedByCurrentUser: false,
      createdAt: new Date().toISOString()
    }));

    component.addComment(post);

    expect(postServiceSpy.addComment).toHaveBeenCalledWith(1, 'Nice post');
    expect(post.commentCount).toBe(1);
  });

  it('should follow user', () => {
    networkServiceSpy.follow.and.returnValue(of({ id: 1, followerId: 1, followingId: 2 }));

    component.follow({ id: 2 });

    expect(networkServiceSpy.follow).toHaveBeenCalledWith(2);
    expect(component.isFollowed(2)).toBeTrue();
  });
});
