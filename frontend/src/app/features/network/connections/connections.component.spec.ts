import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';
import { of } from 'rxjs';

import { ConnectionsComponent } from './connections.component';
import { NetworkService } from '../../../core/services/network.service';

describe('ConnectionsComponent', () => {

  let component: ConnectionsComponent;
  let fixture: ComponentFixture<ConnectionsComponent>;
  let networkServiceSpy: jasmine.SpyObj<NetworkService>;

  const mockApi = (data: any) => ({
    success: true,
    data,
    statusCode: 200,
    timestamp: new Date().toISOString()
  });

  beforeEach(async () => {

    networkServiceSpy = jasmine.createSpyObj('NetworkService', [
      'getConnections',
      'getPendingRequests',
      'getSentRequests',
      'getSuggestedConnections',
      'sendRequest',
      'acceptRequest',
      'rejectRequest',
      'removeConnection'
    ]);

    await TestBed.configureTestingModule({

      imports: [
        CommonModule,
        RouterTestingModule
      ],

      declarations: [
        ConnectionsComponent
      ],

      providers: [
        { provide: NetworkService, useValue: networkServiceSpy }
      ]

    }).compileComponents();

  });

  beforeEach(() => {

    networkServiceSpy.getConnections.and.returnValue(
      of(mockApi([]))
    );

    networkServiceSpy.getPendingRequests.and.returnValue(
      of(mockApi([]))
    );

    networkServiceSpy.getSentRequests.and.returnValue(
      of(mockApi([]))
    );

    networkServiceSpy.getSuggestedConnections.and.returnValue(
      of([])
    );

    fixture = TestBed.createComponent(ConnectionsComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load connections on init', () => {
    expect(networkServiceSpy.getConnections).toHaveBeenCalled();
  });

  it('should send connection request', () => {

    networkServiceSpy.sendRequest.and.returnValue(
      of(mockApi({}))
    );

    component.sendRequest({ id: 1 } as any);

    expect(networkServiceSpy.sendRequest).toHaveBeenCalledWith(1);

  });

  it('should accept connection request', () => {

    networkServiceSpy.acceptRequest.and.returnValue(
      of(mockApi({}))
    );

    const connection: any = { id: 1 };

    component.pendingRequests = [connection];

    component.accept(connection);

    expect(networkServiceSpy.acceptRequest).toHaveBeenCalledWith(1);

  });

  it('should reject connection request', () => {

    networkServiceSpy.rejectRequest.and.returnValue(
      of(mockApi({}))
    );

    const connection: any = { id: 1 };

    component.pendingRequests = [connection];

    component.reject(connection);

    expect(networkServiceSpy.rejectRequest).toHaveBeenCalledWith(1);

  });

  it('should remove connection', () => {

    networkServiceSpy.removeConnection.and.returnValue(
      of(mockApi(null))
    );

    const connection: any = {
      id: 1,
      requesterId: 2,
      addresseeId: 1
    };

    component.currentUserId = 1;
    component.connections = [connection];

    component.removeConnection(connection);

    expect(networkServiceSpy.removeConnection).toHaveBeenCalled();

  });

});
