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

  const mockApi = (data: any) => ({
    success: true,
    data,
    statusCode: 200,
    timestamp: new Date().toISOString()
  });

  beforeEach(async () => {

    userServiceSpy = jasmine.createSpyObj(
      'UserService',
      ['getUserById','updateProfile']
    );

    authServiceSpy = jasmine.createSpyObj(
      'AuthService',
      ['isBusinessOrCreator']
    );

    routerSpy = jasmine.createSpyObj(
      'Router',
      ['navigate']
    );

    await TestBed.configureTestingModule({

      imports: [ReactiveFormsModule],

      declarations: [ProfileEditComponent],

      providers: [

        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },

        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { id: 1 }
            }
          }
        }

      ]

    }).compileComponents();

  });

  beforeEach(() => {

    authServiceSpy.isBusinessOrCreator
      .and.returnValue(false);

    userServiceSpy.getUserById
      .and.returnValue(

        of(mockApi({
          firstName:'John',
          lastName:'Doe',
          bio:'Developer'
        }))

      );

    fixture = TestBed.createComponent(ProfileEditComponent);

    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should create component', () => {

    expect(component).toBeTruthy();

  });

  it('should initialize form', () => {

    expect(component.profileForm).toBeDefined();

  });

  it('should load user profile', () => {

    expect(userServiceSpy.getUserById)
      .toHaveBeenCalledWith(1);

  });

  it('should update profile successfully', () => {

    userServiceSpy.updateProfile
      .and.returnValue(of(mockApi({})));

    component.onSubmit();

    expect(userServiceSpy.updateProfile)
      .toHaveBeenCalled();

    expect(component.saved).toBeTrue();

    expect(component.loading).toBeFalse();

  });

  it('should handle update error', () => {

    userServiceSpy.updateProfile
      .and.returnValue(

        throwError(() => ({
          error:{message:'Update failed'}
        }))

      );

    component.onSubmit();

    expect(component.error)
      .toBe('Update failed');

    expect(component.loading)
      .toBeFalse();

  });

});
