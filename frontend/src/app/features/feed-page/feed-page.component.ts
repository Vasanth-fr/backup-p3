import { Component, OnInit, HostListener } from '@angular/core';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

import { PostService } from '../../core/services/post.service';
import { UserService, UserSummaryResponse } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';

import { Post, FeedResponse } from '../../shared/models/models';

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
  private searchSubject = new Subject<string>();

  constructor(
    private postService: PostService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUserId() ?? 0;
    this.loadFeed();
    this.setupSearch();
  }

  // ================= SEARCH USERS =================

  setupSearch(): void {
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      // P3: searchUsers returns UserSummaryResponse[] directly
      switchMap(q => this.userService.searchUsers(q))
    ).subscribe(users => {
      this.users = users ?? [];
    });
  }

  onSearchInput(value: string): void {
    if (!value.trim()) {
      this.users = [];
      return;
    }
    this.searchSubject.next(value);
  }

  // ================= LOAD FEED =================

  loadFeed(): void {
    this.page = 0;
    this.loading = true;

    // P3: getUserPosts returns FeedResponse directly
    this.postService.getUserPosts(this.currentUserId, this.page, 10)
      .subscribe((res: FeedResponse) => {
        this.posts = (res.posts ?? []).map(p => ({
          ...p,
          comments: [],
          showComments: false,
          newComment: ''
        }));
        this.lastPage = res.currentPage >= res.totalPages - 1;
        this.loading = false;
      });
  }

  // ================= INFINITE SCROLL =================

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

    this.postService.getUserPosts(this.currentUserId, this.page, 10)
      .subscribe((res: FeedResponse) => {
        const newPosts = (res.posts ?? []).map(p => ({
          ...p,
          comments: [],
          showComments: false,
          newComment: ''
        }));
        this.posts = [...this.posts, ...newPosts];
        this.lastPage = res.currentPage >= res.totalPages - 1;
        this.loadingMore = false;
      });
  }

  // ================= CREATE POST =================

  createPost(): void {
    if (!this.newPostContent.trim()) return;
    this.creatingPost = true;

    // P3: createPost returns Post directly
    this.postService.createPost({
      content: this.newPostContent,
      hashtags: this.newPostHashtags
    }).subscribe(post => {
      this.posts.unshift({
        ...post,
        comments: [],
        showComments: false,
        newComment: ''
      });
      this.newPostContent = '';
      this.newPostHashtags = '';
      this.creatingPost = false;
    });
  }

  // ================= LIKE POST =================

  toggleLike(post: any): void {
    if (post.likedByCurrentUser) {
      this.postService.unlikePost(post.id).subscribe(() => {
        post.likedByCurrentUser = false;
        post.likeCount--;
      });
    } else {
      this.postService.likePost(post.id).subscribe(() => {
        post.likedByCurrentUser = true;
        post.likeCount++;
      });
    }
  }

  // ================= TOGGLE COMMENTS =================

  toggleComments(post: any): void {
    post.showComments = !post.showComments;
    if (post.showComments) {
      this.loadComments(post);
    }
  }

  // ================= LOAD COMMENTS =================

  loadComments(post: any): void {
    // P3: getComments returns Comment[] directly
    this.postService.getComments(post.id)
      .subscribe(comments => {
        comments.forEach((c: any) => {
          c.replies = c.replies ?? [];
          c.replyText = '';
        });
        post.comments = comments.reverse();
        post.commentCount = post.comments.length;
      });
  }

  // ================= ADD COMMENT =================

  addComment(post: any): void {
    const text = post.newComment?.trim();
    if (!text) return;

    // P3: addComment returns Comment directly
    this.postService.addComment(post.id, text)
      .subscribe(comment => {
        post.comments.unshift(comment);
        post.commentCount++;
        post.newComment = '';
      });
  }

  // ================= COMMENT LIKE =================

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

  // ================= DELETE COMMENT =================

  deleteComment(comment: any, post: any): void {
    this.postService.deleteComment(comment.id)
      .subscribe(() => {
        post.comments = post.comments.filter((c: any) => c.id !== comment.id);
        post.commentCount--;
      });
  }

  // ================= REPLY =================

  reply(post: any, parent: any): void {
    const content = parent.replyText?.trim();
    if (!content) return;

    // P3: replyToComment returns Comment directly
    this.postService.replyToComment(post.id, parent.id, content)
      .subscribe(comment => {
        parent.replies = parent.replies ?? [];
        parent.replies.push(comment);
        parent.replyText = '';
        post.commentCount++;
      });
  }

  // ================= FOLLOW USER =================

  follow(user: UserSummaryResponse): void {
    this.userService.followUser(user.id).subscribe(() => {
      console.log('Followed', user.email);
    });
  }

  // ================= HASHTAG FORMAT =================

  formatContent(text: string): string[] {
    if (!text) return [];
    return text.split(' ');
  }

  searchHashtag(tag: string): void {
    const clean = tag.replace('#', '');
    this.onSearchInput(clean);
  }
}
