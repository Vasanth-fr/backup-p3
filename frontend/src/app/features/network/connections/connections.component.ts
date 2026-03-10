import { Component, OnInit } from '@angular/core';
import { NetworkConnection, NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserSummaryResponse, UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-connections',
  templateUrl: './connections.component.html',
  styleUrls: ['./connections.component.css']
})
export class ConnectionsComponent implements OnInit {

  tab = 'discover';

  connections: NetworkConnection[] = [];
  pendingRequests: NetworkConnection[] = [];
  sentRequests: NetworkConnection[] = [];
  allUsers: UserSummaryResponse[] = [];

  sentUserIds = new Set<number>();
  currentUserId = 0;

  constructor(
    private networkService: NetworkService,
    private userService: UserService,
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
          if (req.userDetails?.id) {
            this.sentUserIds.add(req.userDetails.id);
          }
        });
      });

    this.userService.searchUsers('')
      .subscribe(users => {
        this.allUsers = users.filter(user => user.id !== this.currentUserId);
      });
  }

  sendRequest(user: UserSummaryResponse): void {
    this.networkService.sendRequest(user.id)
      .subscribe(() => {
        this.sentUserIds.add(user.id);
      });
  }

  accept(c: NetworkConnection): void {
    this.networkService.acceptRequest(c.id)
      .subscribe(connection => {
        this.pendingRequests = this.pendingRequests.filter(r => r.id !== c.id);
        this.connections.push(connection);
      });
  }

  reject(c: NetworkConnection): void {
    this.networkService.rejectRequest(c.id)
      .subscribe(() => {
        this.pendingRequests = this.pendingRequests.filter(r => r.id !== c.id);
      });
  }

  removeConnection(c: NetworkConnection): void {
    this.networkService.removeConnection(c.id)
      .subscribe(() => {
        this.connections = this.connections.filter(conn => conn.id !== c.id);
      });
  }

  removeConnectionFromDiscover(userId: number): void {
    const connection = this.connections.find(c => c.userDetails?.id === userId);
    if (!connection) {
      return;
    }

    this.networkService.removeConnection(connection.id)
      .subscribe(() => {
        this.connections = this.connections.filter(c => c.id !== connection.id);
      });
  }

  isConnected(userId: number): boolean {
    return this.connections.some(c => c.userDetails?.id === userId);
  }

  getDisplayName(user: UserSummaryResponse): string {
    return user.fullName || [user.firstName, user.lastName].filter(Boolean).join(' ') || user.email;
  }

  getDisplayUsername(user: UserSummaryResponse): string {
    return user.username || user.email.split('@')[0];
  }

  getOtherUserName(c: NetworkConnection): string {
    return c.userDetails?.fullName || c.userDetails?.username || 'Unknown User';
  }

  getOtherUserUsername(c: NetworkConnection): string {
    return c.userDetails?.username || 'unknown';
  }

  getOtherUserId(c: NetworkConnection): number {
    return c.userDetails?.id || 0;
  }
}
