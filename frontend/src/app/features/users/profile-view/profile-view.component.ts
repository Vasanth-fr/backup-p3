import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { UserService, UserSummaryResponse } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';

import { Post } from '../../../shared/models/models';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

  profile!: UserSummaryResponse & {
    isFollowing?: boolean;
    followerCount?: number;
    followingCount?: number;
    connectionCount?: number;
    location?: string;
    website?: string;
  };

  posts: Post[] = [];

  userId!: number;
  currentUserId: number = 0;

  loading = true;
  postsLoading = true;
  followLoading = false;
  errorMsg = '';

  isOwnProfile = false;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    private networkService: NetworkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUserId() ?? 0;

    this.route.params.subscribe(params => {
      this.userId = +params['id'];
      this.isOwnProfile = this.userId === this.currentUserId;
      this.errorMsg = '';
      this.posts = [];

      this.loadProfile();
      this.loadPosts();
    });
  }

  // ─── Helpers ─────────────────────────────────────────────────────────

  getDisplayName(p: any): string {
    if (p?.fullName) return p.fullName;
    const parts = [p?.firstName, p?.lastName].filter(Boolean);
    if (parts.length) return parts.join(' ');
    return p?.username || p?.email || 'Unknown User';
  }

  // ─── PROFILE ─────────────────────────────────────────────────────────

  loadProfile(): void {
    this.loading = true;

    const profile$ = this.userService.getUserById(this.userId)
      .pipe(catchError(() => of(null)));
    const following$ = this.isOwnProfile
      ? of(false)
      : this.networkService.isFollowing(this.userId).pipe(catchError(() => of(false)));
    const followerCount$ = this.networkService.getFollowerCount(this.userId).pipe(catchError(() => of(0)));
    const followingCount$ = this.networkService.getFollowingCount(this.userId).pipe(catchError(() => of(0)));
    const connectionCount$ = this.isOwnProfile
      ? this.networkService.getConnectionCount().pipe(catchError(() => of(0)))
      : of(null);

    forkJoin([profile$, following$, followerCount$, followingCount$, connectionCount$]).subscribe({
       next: ([profile, isFollowing, followerCount, followingCount, connectionCount]) => {

         if (!profile) {
           this.errorMsg = 'User not found.';
           this.loading = false;
           return;
         }

         this.profile = {
           ...profile,
           isFollowing,
           followerCount,
           followingCount,
           connectionCount: connectionCount ?? profile.connectionCount ?? 0,
         };

         this.loading = false;
       },
       error: (err) => {
         console.error('Profile load failed:', err);
         this.errorMsg = 'Could not load profile.';
         this.loading = false;
       }
     });
  }

  // ─── POSTS ───────────────────────────────────────────────────────────

  loadPosts(): void {
    this.postsLoading = true;
    this.postService.getUserPosts(this.userId, 0, 20)
      .subscribe({
        next: (response) => {
          this.posts = (response.posts || []).map(p => ({ ...p, comments: [], newComment: '', showComments: false }));
          this.posts.forEach(post => this.loadComments(post));
          this.postsLoading = false;
        },
        error: () => {
          this.postsLoading = false;
        }
      });
  }

  loadComments(post: any): void {
    this.postService.getComments(post.id)
      .subscribe({
        next: (comments) => { post.comments = comments; },
        error: () => { post.comments = []; }
      });
  }

  deletePost(post: Post): void {
    if (!confirm('Delete this post?')) return;
    this.postService.deletePost(post.id).subscribe(() => {
      this.posts = this.posts.filter(p => p.id !== post.id);
    });
  }

  // ─── FOLLOW ──────────────────────────────────────────────────────────

  toggleFollow(): void {
    if (this.followLoading) return;
    this.followLoading = true;

    if (this.profile.isFollowing) {
      this.networkService.unfollow(this.userId).subscribe({
        next: () => {
          this.profile.isFollowing = false;
          if ((this.profile.followerCount ?? 0) > 0) this.profile.followerCount!--;
          this.followLoading = false;
        },
        error: () => { this.followLoading = false; }
      });
    } else {
      this.networkService.follow(this.userId).subscribe({
        next: () => {
          this.profile.isFollowing = true;
          this.profile.followerCount = (this.profile.followerCount ?? 0) + 1;
          this.followLoading = false;
        },
        error: () => { this.followLoading = false; }
      });
    }
  }

  // ─── LIKE ────────────────────────────────────────────────────────────

  toggleLike(post: any): void {
    if (post.likedByCurrentUser) {
      this.postService.unlikePost(post.id).subscribe({
        next: () => {
          post.likedByCurrentUser = false;
          if (post.likeCount > 0) post.likeCount--;
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 404) {
            post.likedByCurrentUser = false;
            if (post.likeCount > 0) post.likeCount--;
          }
        }
      });
    } else {
      this.postService.likePost(post.id).subscribe({
        next: () => {
          post.likedByCurrentUser = true;
          post.likeCount++;
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 409) {
            post.likedByCurrentUser = true;
          }
        }
      });
    }
  }

  // ─── COMMENT ─────────────────────────────────────────────────────────

  toggleComments(post: any): void {
    post.showComments = !post.showComments;
    if (post.showComments && (!post.comments || post.comments.length === 0)) {
      this.loadComments(post);
    }
  }

  addComment(post: any): void {
    if (!post.newComment?.trim()) return;
    const text = post.newComment.trim();
    this.postService.addComment(post.id, text)
      .subscribe(comment => {
        post.comments = [comment, ...(post.comments || [])];
        post.commentCount = (post.commentCount || 0) + 1;
        post.newComment = '';
      });
  }

  deleteComment(comment: any, post: any): void {
    this.postService.deleteComment(comment.id).subscribe(() => {
      post.comments = post.comments.filter((c: any) => c.id !== comment.id);
      if (post.commentCount > 0) post.commentCount--;
    });
  }

  // ─── UTILS ───────────────────────────────────────────────────────────

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Math.floor((Date.now() - new Date(dateStr).getTime()) / 1000);
    if (diff < 60) return `${diff}s ago`;
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
  }
}
