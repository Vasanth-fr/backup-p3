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

  const mockApi = (data: any) => ({
    success: true,
    data,
    statusCode: 200,
    timestamp: new Date().toISOString()
  });

  beforeEach(async () => {

    userServiceSpy = jasmine.createSpyObj('UserService', [
      'getUserById'
    ]);

    postServiceSpy = jasmine.createSpyObj('PostService', [
      'getUserPosts',
      'getComments',
      'likePost',
      'unlikePost',
      'addComment'
    ]);

    networkServiceSpy = jasmine.createSpyObj('NetworkService', [
      'follow',
      'unfollow'
    ]);

    authServiceSpy = jasmine.createSpyObj('AuthService', [
      'getCurrentUserId'
    ]);

    await TestBed.configureTestingModule({

      imports: [
        FormsModule,
        RouterTestingModule
      ],

      declarations: [
        ProfileViewComponent
      ],

      providers: [

        { provide: UserService, useValue: userServiceSpy },
        { provide: PostService, useValue: postServiceSpy },
        { provide: NetworkService, useValue: networkServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },

        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: 1 })
          }
        }

      ]

    }).compileComponents();

  });

  beforeEach(() => {

    authServiceSpy.getCurrentUserId.and.returnValue(2);

    userServiceSpy.getUserById.and.returnValue(
      of(mockApi({
        id: 1,
        fullName: 'John Doe',
        bio: 'Developer',
        followerCount: 5,
        followingCount: 3,
        connectionCount: 2,
        isFollowing: false
      }))
    );

    postServiceSpy.getUserPosts.and.returnValue(
      of(mockApi({
        content: [],
        last: true
      }))
    );

    fixture = TestBed.createComponent(ProfileViewComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load profile', () => {
    expect(userServiceSpy.getUserById).toHaveBeenCalledWith(1);
  });

  it('should load posts', () => {
    expect(postServiceSpy.getUserPosts).toHaveBeenCalled();
  });

  it('should follow user', () => {

    networkServiceSpy.follow.and.returnValue(
      of(mockApi({
        id: 1,
        requesterId: 2,
        addresseeId: 1,
        status: 'ACCEPTED'
      }))
    );

    component.profile.isFollowing = false;

    component.toggleFollow();

    expect(networkServiceSpy.follow).toHaveBeenCalledWith(1);

  });

  it('should unfollow user', () => {

    networkServiceSpy.unfollow.and.returnValue(
      of({
        success: true,
        data: undefined,
        statusCode: 200,
        timestamp: new Date().toISOString()
      })
    );

    component.profile.isFollowing = true;

    component.toggleFollow();

    expect(networkServiceSpy.unfollow).toHaveBeenCalledWith(1);

  });

});
