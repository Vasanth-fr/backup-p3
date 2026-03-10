import { Component, OnInit } from '@angular/core';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';
import { Connection, User } from '../../../shared/models/models';

@Component({
  selector: 'app-connections',
  templateUrl: './connections.component.html',
  styleUrls: ['./connections.component.css']
})
export class ConnectionsComponent implements OnInit {

  tab = 'discover';

  connections: Connection[] = [];
  pendingRequests: Connection[] = [];
  sentRequests: Connection[] = [];
  allUsers: User[] = [];

  sentUserIds = new Set<number>();
  currentUserId = 0;

  constructor(
    private networkService: NetworkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUserId() ?? 0;

    // P3: all network endpoints return data directly (no ApiResponse wrapper)
    this.networkService.getConnections()
      .subscribe(connections => {
        this.connections = connections;
      });

    this.networkService.getPendingRequests()
      .subscribe(requests => {
        this.pendingRequests = requests;
      });

    this.networkService.getSentRequests()
      .subscribe(requests => {
        this.sentRequests = requests;
        this.sentRequests.forEach(req => {
          this.sentUserIds.add(req.addresseeId);
        });
      });

    this.networkService.getSuggestedConnections(50)
      .subscribe((users: any[]) => {
        this.allUsers = users;
      });
  }

  sendRequest(user: User): void {
    this.networkService.sendRequest(user.id)
      .subscribe(() => {
        this.sentUserIds.add(user.id);
      });
  }

  accept(c: Connection): void {
    this.networkService.acceptRequest(c.id)
      .subscribe(() => {
        this.pendingRequests = this.pendingRequests.filter(r => r.id !== c.id);
        this.connections.push({ ...c, status: 'ACCEPTED' });
      });
  }

  reject(c: Connection): void {
    this.networkService.rejectRequest(c.id)
      .subscribe(() => {
        this.pendingRequests = this.pendingRequests.filter(r => r.id !== c.id);
      });
  }

  removeConnection(c: Connection): void {
    const otherUserId = this.getOtherUserId(c);
    this.networkService.removeConnection(otherUserId)
      .subscribe(() => {
        this.connections = this.connections.filter(conn => conn.id !== c.id);
      });
  }

  removeConnectionFromDiscover(userId: number): void {
    this.networkService.removeConnection(userId)
      .subscribe(() => {
        this.connections = this.connections.filter(c =>
          c.requesterId !== userId && c.addresseeId !== userId
        );
      });
  }

  isConnected(userId: number): boolean {
    return this.connections.some(c =>
      c.requesterId === userId || c.addresseeId === userId
    );
  }

  getOtherUserName(c: Connection): string {
    return c.requesterId === this.currentUserId
      ? (c.addresseeFullName || c.addresseeUsername)
      : (c.requesterFullName || c.requesterUsername);
  }

  getOtherUserUsername(c: Connection): string {
    return c.requesterId === this.currentUserId
      ? c.addresseeUsername
      : c.requesterUsername;
  }

  getOtherUserId(c: Connection): number {
    return c.requesterId === this.currentUserId
      ? c.addresseeId
      : c.requesterId;
  }
}
