import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';

describe('RegisterComponent', () => {

  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {

    authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({

      imports: [
        ReactiveFormsModule,
        RouterTestingModule
      ],

      declarations: [
        RegisterComponent
      ],

      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]

    }).compileComponents();

  });

  beforeEach(() => {

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should register user', () => {

    authServiceSpy.register.and.returnValue(
      of({
        accessToken: 'test-token',
        refreshToken: 'refresh-token',
        tokenType: 'Bearer',
        userId: 1,
        email: 'test@test.com'
      })
    );

    component.registerForm.setValue({
      email: 'test@test.com',
      password: 'password123',
      firstName: 'Test',
      lastName: 'User'
    });

    component.onSubmit();

    expect(authServiceSpy.register).toHaveBeenCalled();

  });

});
