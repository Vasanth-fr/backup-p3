import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { ProfileEditComponent } from './profile-edit.component';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

describe('ProfileEditComponent', () => {
  let component: ProfileEditComponent;
  let fixture: ComponentFixture<ProfileEditComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getCurrentUserProfile', 'updateProfile']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUserId']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    authServiceSpy.getCurrentUserId.and.returnValue(1);
    userServiceSpy.getCurrentUserProfile.and.returnValue(of({
      id: 1,
      email: 'john@example.com',
      firstName: 'John',
      lastName: 'Doe',
      bio: 'Developer',
      location: 'Hyderabad'
    }));

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [ProfileEditComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: 1 } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load current user profile', () => {
    expect(userServiceSpy.getCurrentUserProfile).toHaveBeenCalled();
    expect(component.profileForm.value.firstName).toBe('John');
  });

  it('should update profile successfully', () => {
    userServiceSpy.updateProfile.and.returnValue(of({
      id: 1,
      email: 'john@example.com'
    }));

    component.onSubmit();

    expect(userServiceSpy.updateProfile).toHaveBeenCalled();
    expect(component.saved).toBeTrue();
    expect(component.loading).toBeFalse();
  });

  it('should handle update error', () => {
    userServiceSpy.updateProfile.and.returnValue(
      throwError(() => ({ error: { message: 'Update failed' } }))
    );

    component.onSubmit();

    expect(component.error).toBe('Update failed');
    expect(component.loading).toBeFalse();
  });
});
