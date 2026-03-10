import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Connection, Follow, User } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NetworkService {

  private readonly API = `${environment.apiUrl}/network`;

  constructor(private http: HttpClient) {}

  // ================= CONNECTIONS =================
  // P3: all endpoints return data directly

  getConnections(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.API}/connections`);
  }

  getPendingRequests(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.API}/requests/received`);
  }

  getSentRequests(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.API}/requests/sent`);
  }

  sendRequest(userId: number): Observable<Connection> {
    return this.http.post<Connection>(`${this.API}/connect/${userId}`, {});
  }

  acceptRequest(connectionId: number): Observable<Connection> {
    return this.http.put<Connection>(`${this.API}/connections/${connectionId}/accept`, {});
  }

  rejectRequest(connectionId: number): Observable<Connection> {
    return this.http.put<Connection>(`${this.API}/connections/${connectionId}/reject`, {});
  }

  removeConnection(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/connect/${userId}`);
  }

  // ================= FOLLOW =================

  follow(userId: number): Observable<Follow> {
    return this.http.post<Follow>(`${this.API}/follow/${userId}`, {});
  }

  unfollow(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/follow/${userId}`);
  }

  getFollowers(userId: number): Observable<Follow[]> {
    return this.http.get<Follow[]>(`${this.API}/followers/${userId}`);
  }

  getFollowing(userId: number): Observable<Follow[]> {
    return this.http.get<Follow[]>(`${this.API}/following/${userId}`);
  }

  isFollowing(userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.API}/is-following/${userId}`);
  }

  getFollowerCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.API}/follower-count/${userId}`);
  }

  getFollowingCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.API}/following-count/${userId}`);
  }

  // ================= DISCOVERY =================

  getSuggestedConnections(limit: number = 10): Observable<User[]> {
    return this.http.get<User[]>(`${this.API}/suggestions?limit=${limit}`);
  }

  getMutualConnections(userId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.API}/mutual-connections/${userId}`);
  }

  searchUsers(query: string, page: number = 0, size: number = 20): Observable<User[]> {
    return this.http.get<User[]>(`${this.API}/search?q=${query}&page=${page}&size=${size}`);
  }

  getPeopleYouMayKnow(limit: number = 10): Observable<User[]> {
    return this.http.get<User[]>(`${this.API}/people-you-may-know?limit=${limit}`);
  }
}
