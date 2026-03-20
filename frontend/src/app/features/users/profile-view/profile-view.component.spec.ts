import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProfileViewComponent } from './profile-view.component';
import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';

describe('ProfileViewComponent', () => {
  let component: ProfileViewComponent;
  let fixture: ComponentFixture<ProfileViewComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let networkServiceSpy: jasmine.SpyObj<NetworkService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUserById']);
    postServiceSpy = jasmine.createSpyObj('PostService', ['getUserPosts', 'getComments', 'likePost', 'unlikePost', 'addComment', 'deleteComment', 'deletePost']);
    networkServiceSpy = jasmine.createSpyObj('NetworkService', ['follow', 'unfollow', 'isFollowing', 'getFollowerCount', 'getFollowingCount', 'getConnectionCount']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUserId']);

    authServiceSpy.getCurrentUserId.and.returnValue(2);
    userServiceSpy.getUserById.and.returnValue(of({
      id: 1,
      email: 'john@example.com',
      fullName: 'John Doe',
      bio: 'Developer',
      followerCount: 5,
      followingCount: 3,
      connectionCount: 2
    }));
    postServiceSpy.getUserPosts.and.returnValue(of({
      posts: [],
      totalElements: 0,
      totalPages: 1,
      currentPage: 0
    }));
    networkServiceSpy.isFollowing.and.returnValue(of(false));
    networkServiceSpy.getFollowerCount.and.returnValue(of(5));
    networkServiceSpy.getFollowingCount.and.returnValue(of(3));
    networkServiceSpy.getConnectionCount.and.returnValue(of(7));

    await TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule],
      declarations: [ProfileViewComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: PostService, useValue: postServiceSpy },
        { provide: NetworkService, useValue: networkServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ActivatedRoute, useValue: { params: of({ id: 1 }) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load profile and posts', () => {
    expect(userServiceSpy.getUserById).toHaveBeenCalledWith(1);
    expect(postServiceSpy.getUserPosts).toHaveBeenCalledWith(1, 0, 20);
    expect(networkServiceSpy.getFollowerCount).toHaveBeenCalledWith(1);
    expect(networkServiceSpy.getFollowingCount).toHaveBeenCalledWith(1);
  });

  it('should use network counts for profile stats', () => {
    expect(component.profile.followerCount).toBe(5);
    expect(component.profile.followingCount).toBe(3);
    expect(component.profile.connectionCount).toBe(7);
  });

  it('should follow user', () => {
    networkServiceSpy.follow.and.returnValue(of({ id: 1, followerId: 2, followingId: 1 }));
    component.profile.isFollowing = false;

    component.toggleFollow();

    expect(networkServiceSpy.follow).toHaveBeenCalledWith(1);
    expect(component.profile.isFollowing).toBeTrue();
  });

  it('should unfollow user', () => {
    networkServiceSpy.unfollow.and.returnValue(of(void 0));
    component.profile.isFollowing = true;

    component.toggleFollow();

    expect(networkServiceSpy.unfollow).toHaveBeenCalledWith(1);
    expect(component.profile.isFollowing).toBeFalse();
  });
});
