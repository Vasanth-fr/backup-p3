import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FeedPageComponent } from './feed-page.component';
import { PostService } from '../../core/services/post.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';

describe('FeedPageComponent', () => {

  let component: FeedPageComponent;
  let fixture: ComponentFixture<FeedPageComponent>;

  let postServiceSpy: jasmine.SpyObj<PostService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockApi = (data: any) => ({
    success: true,
    data,
    statusCode: 200,
    timestamp: new Date().toISOString()
  });

  beforeEach(async () => {

    postServiceSpy = jasmine.createSpyObj('PostService', [
      'getUserPosts',
      'createPost',
      'likePost',
      'unlikePost',
      'getComments',
      'addComment',
      'deleteComment',
      'replyToComment',
      'likeComment',
      'unlikeComment'
    ]);

    userServiceSpy = jasmine.createSpyObj('UserService', [
      'searchUsers',
      'followUser'
    ]);

    authServiceSpy = jasmine.createSpyObj('AuthService', [
      'getCurrentUserId'
    ]);

    await TestBed.configureTestingModule({

      imports: [
        CommonModule,
        FormsModule,
        RouterTestingModule
      ],

      declarations: [
        FeedPageComponent
      ],

      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]

    }).compileComponents();

  });

  beforeEach(() => {

    fixture = TestBed.createComponent(FeedPageComponent);
    component = fixture.componentInstance;

    authServiceSpy.getCurrentUserId.and.returnValue(1);

    postServiceSpy.getUserPosts.and.returnValue(
      of(mockApi({
        content: [],
        last: true
      }))
    );

    fixture.detectChanges();

  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load feed on init', () => {
    expect(postServiceSpy.getUserPosts).toHaveBeenCalled();
  });

  it('should search users', fakeAsync(() => {

    userServiceSpy.searchUsers.and.returnValue(
      of(mockApi([{ id: 1, username: 'john' }]))
    );

    component.onSearchInput('john');

    tick(500);

    expect(userServiceSpy.searchUsers).toHaveBeenCalled();

  }));

  it('should create post', () => {

    component.newPostContent = 'Test Post';

    postServiceSpy.createPost.and.returnValue(
      of(mockApi({
        id: 1,
        content: 'Test Post',
        likeCount: 0
      }))
    );

    component.createPost();

    expect(postServiceSpy.createPost).toHaveBeenCalled();

  });

  it('should like post', () => {

    const post: any = {
      id: 1,
      likedByCurrentUser: false,
      likeCount: 0
    };

    postServiceSpy.likePost.and.returnValue(
      of(mockApi({}))
    );

    component.toggleLike(post);

    expect(postServiceSpy.likePost).toHaveBeenCalledWith(1);

  });

  it('should unlike post', () => {

    const post: any = {
      id: 1,
      likedByCurrentUser: true,
      likeCount: 1
    };

    postServiceSpy.unlikePost.and.returnValue(
      of(mockApi({}))
    );

    component.toggleLike(post);

    expect(postServiceSpy.unlikePost).toHaveBeenCalledWith(1);

  });

  it('should add comment', () => {

    const post: any = {
      id: 1,
      comments: [],
      commentCount: 0,
      newComment: 'Nice post'
    };

    postServiceSpy.addComment.and.returnValue(
      of(mockApi({
        id: 1,
        content: 'Nice post'
      }))
    );

    component.addComment(post);

    expect(postServiceSpy.addComment).toHaveBeenCalled();

  });

  it('should follow user', () => {

    const user = { id: 1, username: 'john' } as any;

    userServiceSpy.followUser.and.returnValue(
      of(mockApi({}))
    );

    component.follow(user);

    expect(userServiceSpy.followUser).toHaveBeenCalledWith(1);

  });

});
