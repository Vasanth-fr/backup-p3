import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { AppComponent } from './app.component';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';

describe('AppComponent', () => {

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  let userSubject = new BehaviorSubject<any>(null);

  const authServiceMock = {
    currentUser$: userSubject.asObservable()
  };

  beforeEach(async () => {

    notificationServiceSpy = jasmine.createSpyObj(
      'NotificationService',
      ['startPolling']
    );

    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule
      ],

      declarations: [
        AppComponent
      ],

      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ],

      schemas: [CUSTOM_ELEMENTS_SCHEMA]   // ⭐ FIX HERE

    }).compileComponents();

  });

  beforeEach(() => {

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should set isLoggedIn to false when user is null', () => {
    expect(component.isLoggedIn).toBeFalse();
  });

  it('should set isLoggedIn to true when user exists', () => {

    userSubject.next({ userId: 1 });

    fixture.detectChanges();

    expect(component.isLoggedIn).toBeTrue();

  });

  it('should start notification polling when user logs in', () => {

    userSubject.next({ userId: 1 });

    fixture.detectChanges();

    expect(notificationServiceSpy.startPolling).toHaveBeenCalled();

  });

});
