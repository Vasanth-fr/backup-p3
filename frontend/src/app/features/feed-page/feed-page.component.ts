import { Component, OnInit, HostListener } from '@angular/core';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

import { PostService } from '../../core/services/post.service';
import { UserService, UserSummaryResponse } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { NetworkService } from '../../core/services/network.service';

import { FeedResponse } from '../../shared/models/models';

@Component({
  selector: 'app-feed-page',
  templateUrl: './feed-page.component.html'
})
export class FeedPageComponent implements OnInit {

  posts: any[] = [];

  page = 0;
  lastPage = false;

  loading = true;
  loadingMore = false;
  creatingPost = false;

  currentUserId = 0;

  newPostContent = '';
  newPostHashtags = '';

  users: UserSummaryResponse[] = [];
  followedUserIds = new Set<number>();

  private searchSubject = new Subject<string>();

  constructor(
    private postService: PostService,
    private userService: UserService,
    private authService: AuthService,
    private networkService: NetworkService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUserId() ?? 0;
    this.loadFeed();
    this.setupSearch();
  }

  // ─── SEARCH ──────────────────────────────────────────────────────────

  setupSearch(): void {
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(q => this.userService.searchUsers(q))
    ).subscribe(users => {
      this.users = (users ?? []).filter(u => u.id !== this.currentUserId);
    });
  }

  onSearchInput(value: string): void {
    if (!value.trim()) { this.users = []; return; }
    this.searchSubject.next(value);
  }

  // ─── FEED ────────────────────────────────────────────────────────────

  loadFeed(): void {
    this.page = 0;
    this.loading = true;

    this.postService.getFeed(this.page, 10).subscribe({
      next: (res: FeedResponse) => {
        this.posts = (res.posts ?? []).map(p => ({
          ...p, comments: [], showComments: false, newComment: ''
        }));
        this.lastPage = res.currentPage >= res.totalPages - 1;
        this.loading = false;
      },
      error: () => { this.posts = []; this.loading = false; }
    });
  }

  // ─── INFINITE SCROLL ─────────────────────────────────────────────────

  @HostListener('window:scroll')
  onScroll(): void {
    if (this.loadingMore || this.lastPage) return;
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 200) {
      this.loadMore();
    }
  }

  loadMore(): void {
    if (this.loadingMore) return;
    this.loadingMore = true;
    this.page++;

    this.postService.getFeed(this.page, 10).subscribe({
      next: (res: FeedResponse) => {
        const newPosts = (res.posts ?? []).map(p => ({
          ...p, comments: [], showComments: false, newComment: ''
        }));
        this.posts = [...this.posts, ...newPosts];
        this.lastPage = res.currentPage >= res.totalPages - 1;
        this.loadingMore = false;
      },
      error: () => { this.loadingMore = false; }
    });
  }

  // ─── CREATE POST ─────────────────────────────────────────────────────

  createPost(): void {
    if (!this.newPostContent.trim()) return;
    this.creatingPost = true;

    this.postService.createPost({
      content: this.newPostContent,
      hashtags: this.newPostHashtags || undefined
    }).subscribe({
      next: post => {
        this.posts.unshift({ ...post, comments: [], showComments: false, newComment: '' });
        this.newPostContent = '';
        this.newPostHashtags = '';
        this.creatingPost = false;
      },
      error: () => { this.creatingPost = false; }
    });
  }

  deletePost(post: any): void {
    if (!confirm('Delete this post?')) return;
    this.postService.deletePost(post.id).subscribe(() => {
      this.posts = this.posts.filter(p => p.id !== post.id);
    });
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

  // ─── COMMENTS ────────────────────────────────────────────────────────

  toggleComments(post: any): void {
    post.showComments = !post.showComments;
    if (post.showComments && post.comments.length === 0) {
      this.loadComments(post);
    }
  }

  loadComments(post: any): void {
    this.postService.getComments(post.id).subscribe(comments => {
      comments.forEach((c: any) => { c.replies = c.replies ?? []; c.replyText = ''; });
      post.comments = comments.reverse();
      post.commentCount = post.comments.length;
    });
  }

  addComment(post: any): void {
    const text = post.newComment?.trim();
    if (!text) return;
    this.postService.addComment(post.id, text).subscribe(comment => {
      post.comments.unshift(comment);
      post.commentCount++;
      post.newComment = '';
    });
  }

  deleteComment(comment: any, post: any): void {
    this.postService.deleteComment(comment.id).subscribe(() => {
      post.comments = post.comments.filter((c: any) => c.id !== comment.id);
      if (post.commentCount > 0) post.commentCount--;
    });
  }

  likeComment(comment: any): void {
    if (comment.likedByCurrentUser) {
      comment.likedByCurrentUser = false;
      comment.likeCount--;
      this.postService.unlikeComment(comment.id).subscribe();
    } else {
      comment.likedByCurrentUser = true;
      comment.likeCount++;
      this.postService.likeComment(comment.id).subscribe();
    }
  }

  reply(post: any, parent: any): void {
    const content = parent.replyText?.trim();
    if (!content) return;
    this.postService.replyToComment(post.id, parent.id, content).subscribe(comment => {
      parent.replies = parent.replies ?? [];
      parent.replies.push(comment);
      parent.replyText = '';
      post.commentCount++;
    });
  }

  // ─── FOLLOW ──────────────────────────────────────────────────────────

  follow(user: any): void {
    if (!user?.id || this.followedUserIds.has(user.id)) return;
    this.networkService.follow(user.id).subscribe(() => {
      this.followedUserIds.add(user.id);
    });
  }

  isFollowed(userId: number): boolean {
    return this.followedUserIds.has(userId);
  }

  // ─── UTILS ───────────────────────────────────────────────────────────

  formatContent(text: string): string[] {
    if (!text) return [];
    return text.split(' ');
  }

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Math.floor((Date.now() - new Date(dateStr).getTime()) / 1000);
    if (diff < 60) return `${diff}s ago`;
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
  }

  getDisplayName(user: any): string {
    if (!user) return 'Unknown';
    if (user.fullName) return user.fullName;
    const parts = [user.firstName, user.lastName].filter(Boolean);
    if (parts.length) return parts.join(' ');
    return user.username || user.email || 'Unknown';
  }
}
