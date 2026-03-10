import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  Post,
  CreatePostRequest,
  PostAnalytics,
  Comment,
  FeedResponse
} from '../../shared/models/models';

// P3 interaction-service comment request shape
export interface CommentRequest {
  postId: number;
  content: string;
  parentCommentId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private readonly API = `${environment.apiUrl}/posts`;
  private readonly INTERACTION_API = `${environment.apiUrl}/interactions`;

  constructor(private http: HttpClient) {}

  // ================= CREATE POST =================
  createPost(data: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>(this.API, data);
  }

  // ================= GET SINGLE POST =================
  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.API}/${id}`);
  }

  // ================= UPDATE POST =================
  updatePost(id: number, data: Partial<Post>): Observable<Post> {
    return this.http.put<Post>(`${this.API}/${id}`, data);
  }

  // ================= DELETE POST =================
  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  // ================= USER POSTS =================
  getUserPosts(userId: number, page: number = 0, size: number = 10): Observable<FeedResponse> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<FeedResponse>(`${this.API}/user/${userId}`, { params });
  }

  // ================= FEED =================
  getFeed(page: number = 0, size: number = 20): Observable<FeedResponse> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<FeedResponse>(`${this.API}/feed`, { params });
  }

  // ================= TRENDING POSTS =================
  getTrendingPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.API}/trending`);
  }

  // ================= SEARCH BY HASHTAG =================
  searchByHashtag(hashtag: string, page: number = 0): Observable<FeedResponse> {
    const params = new HttpParams().set('hashtag', hashtag).set('page', page);
    return this.http.get<FeedResponse>(`${this.API}/search`, { params });
  }

  // ================= LIKE / UNLIKE POST =================
  // P3: interaction-service at /api/interactions/likes
  likePost(postId: number): Observable<any> {
    return this.http.post<any>(`${this.INTERACTION_API}/likes`, { postId });
  }

  unlikePost(postId: number): Observable<any> {
    return this.http.delete<any>(`${this.INTERACTION_API}/likes/${postId}`);
  }

  // ================= POST ANALYTICS =================
  getAnalytics(postId: number): Observable<PostAnalytics> {
    return this.http.get<PostAnalytics>(`${this.API}/${postId}/analytics`);
  }

  // ================= COMMENTS =================
  // P3: interaction-service at /api/interactions/comments
  addComment(postId: number, content: string, parentCommentId?: number): Observable<Comment> {
    const body: CommentRequest = { postId, content, ...(parentCommentId ? { parentCommentId } : {}) };
    return this.http.post<Comment>(`${this.INTERACTION_API}/comments`, body);
  }

  replyToComment(postId: number, parentId: number, content: string): Observable<Comment> {
    return this.addComment(postId, content, parentId);
  }

  getComments(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.INTERACTION_API}/comments/post/${postId}`);
  }

  updateComment(commentId: number, content: string): Observable<Comment> {
    return this.http.put<Comment>(`${this.INTERACTION_API}/comments/${commentId}`, { content });
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.INTERACTION_API}/comments/${commentId}`);
  }

  // ================= LIKE COMMENT =================
  likeComment(commentId: number): Observable<any> {
    return this.http.post<any>(`${this.INTERACTION_API}/comments/${commentId}/like`, {});
  }

  unlikeComment(commentId: number): Observable<any> {
    return this.http.delete<any>(`${this.INTERACTION_API}/comments/${commentId}/like`);
  }

  // ================= REPOST =================
  repost(originalPostId: number, content: string): Observable<Post> {
    return this.createPost({ content, originalPostId, type: 'REPOST' });
  }
}
