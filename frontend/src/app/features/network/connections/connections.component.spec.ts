import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ConnectionsComponent } from './connections.component';
import { NetworkService } from '../../../core/services/network.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

describe('ConnectionsComponent', () => {

  let component: ConnectionsComponent;
  let fixture: ComponentFixture<ConnectionsComponent>;
  let networkServiceSpy: jasmine.SpyObj<NetworkService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    networkServiceSpy = jasmine.createSpyObj('NetworkService', [
      'getConnections',
      'getPendingRequests',
      'getSentRequests',
      'sendRequest',
      'acceptRequest',
      'rejectRequest',
      'removeConnection'
    ]);

    userServiceSpy = jasmine.createSpyObj('UserService', ['searchUsers']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUserId']);

    networkServiceSpy.getConnections.and.returnValue(of([]));
    networkServiceSpy.getPendingRequests.and.returnValue(of([]));
    networkServiceSpy.getSentRequests.and.returnValue(of([]));
    userServiceSpy.searchUsers.and.returnValue(of([]));
    authServiceSpy.getCurrentUserId.and.returnValue(1);

    await TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule],
      declarations: [ConnectionsComponent],
      providers: [
        { provide: NetworkService, useValue: networkServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConnectionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load connection data on init', () => {
    expect(networkServiceSpy.getConnections).toHaveBeenCalled();
    expect(networkServiceSpy.getPendingRequests).toHaveBeenCalled();
    expect(networkServiceSpy.getSentRequests).toHaveBeenCalled();
    expect(userServiceSpy.searchUsers).toHaveBeenCalledWith('');
  });

  it('should add user to sent ids when sending request', () => {
    networkServiceSpy.sendRequest.and.returnValue(of({
      id: 10,
      userId: 1,
      connectedUserId: 2,
      status: 'PENDING',
      userDetails: { id: 2, username: 'alex' }
    } as any));

    component.sendRequest({ id: 2 } as any);

    expect(networkServiceSpy.sendRequest).toHaveBeenCalledWith(2);
    expect(component.sentUserIds.has(2)).toBeTrue();
    expect(component.sentRequests.length).toBe(1);
  });

  it('should move accepted request into connections', () => {
    const pending = {
      id: 4,
      userId: 2,
      connectedUserId: 1,
      status: 'PENDING',
      userDetails: { id: 2, username: 'alex' }
    } as any;

    networkServiceSpy.acceptRequest.and.returnValue(of({
      ...pending,
      status: 'ACCEPTED'
    }));

    component.pendingRequests = [pending];
    component.accept(pending);

    expect(networkServiceSpy.acceptRequest).toHaveBeenCalledWith(4);
    expect(component.pendingRequests.length).toBe(0);
    expect(component.connections.length).toBe(1);
    expect(component.connections[0].status).toBe('ACCEPTED');
  });

  it('should remove connection from list', () => {
    const connection = {
      id: 5,
      userDetails: { id: 2, username: 'alex' }
    } as any;

    networkServiceSpy.removeConnection.and.returnValue(of(void 0));
    component.connections = [connection];
    component.sentUserIds.add(2);

    component.removeConnection(connection);

    expect(networkServiceSpy.removeConnection).toHaveBeenCalledWith(5);
    expect(component.connections.length).toBe(0);
    expect(component.sentUserIds.has(2)).toBeFalse();
  });
});
