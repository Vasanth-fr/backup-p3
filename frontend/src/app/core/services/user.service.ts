import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { User, UserRole, PrivacyType } from '../../shared/models/models';

// P3 user-service returns UserProfileResponse shape
export interface UserProfileResponse {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  bio?: string;
  profilePicture?: string;
  location?: string;
  website?: string;
  role?: UserRole;
  privacy?: PrivacyType;
}

export interface UserSummaryResponse {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  username?: string;
  bio?: string;
  profilePicture?: string;
  followerCount?: number;
  followingCount?: number;
  connectionCount?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API = `${environment.apiUrl}/users`;
  private readonly NETWORK_API = `${environment.apiUrl}/network`;

  constructor(private http: HttpClient) {}

  // ================= CURRENT USER PROFILE =================
  // P3: GET /api/users/profile  (requires X-User-Email header via gateway)
  getCurrentUserProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.API}/profile`);
  }

  // Alias kept for components still calling getCurrentUser()
  getCurrentUser(): Observable<UserProfileResponse> {
    return this.getCurrentUserProfile();
  }

  // ================= GET USER BY ID =================
  getUserById(id: number): Observable<UserSummaryResponse> {
    return this.http.get<UserSummaryResponse>(`${this.API}/${id}`);
  }

  // ================= UPDATE PROFILE =================
  updateProfile(data: Partial<UserProfileResponse>): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.API}/profile`, data);
  }

  // ================= SEARCH USERS =================
  searchUsers(query: string): Observable<UserSummaryResponse[]> {
    return this.http.get<UserSummaryResponse[]>(`${this.API}/search?query=${query}`);
  }

  // ================= FOLLOW / UNFOLLOW =================
  followUser(userId: number): Observable<any> {
    return this.http.post<any>(`${this.NETWORK_API}/follow/${userId}`, {});
  }

  unfollowUser(userId: number): Observable<any> {
    return this.http.delete<any>(`${this.NETWORK_API}/follow/${userId}`);
  }
}
