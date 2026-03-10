import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse, User } from '../../shared/models/models';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API = `${environment.apiUrl}/users`;
  private readonly NETWORK_API = `${environment.apiUrl}/network`;

  constructor(private http: HttpClient) {}

  // ================= CURRENT USER =================
  getCurrentUser(): Observable<ApiResponse<User>> {

    return this.http.get<ApiResponse<User>>(
      `${this.API}/me`
    );

  }

  // ================= GET USER BY ID =================
  getUserById(id: number): Observable<ApiResponse<User>> {

    return this.http.get<ApiResponse<User>>(
      `${this.API}/${id}`
    );

  }

  // ================= UPDATE PROFILE =================
  updateProfile(
    id: number,
    data: Partial<User>
  ): Observable<ApiResponse<User>> {

    return this.http.put<ApiResponse<User>>(
      `${this.API}/${id}`,
      data
    );

  }

  // ================= SEARCH USERS =================
  searchUsers(query: string): Observable<ApiResponse<User[]>> {

    return this.http.get<ApiResponse<User[]>>(
      `${this.API}/search?q=${query}`
    );

  }

  // ================= FOLLOW USER =================
  followUser(userId: number): Observable<ApiResponse<any>> {

    return this.http.post<ApiResponse<any>>(
      `${this.NETWORK_API}/follow/${userId}`,
      {}
    );

  }

  // ================= UNFOLLOW USER =================
  unfollowUser(userId: number): Observable<ApiResponse<any>> {

    return this.http.delete<ApiResponse<any>>(
      `${this.NETWORK_API}/follow/${userId}`
    );

  }

}
