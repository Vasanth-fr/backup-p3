import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { UserService, UserSummaryResponse } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { NetworkService } from '../../../core/services/network.service';
import { AuthService } from '../../../core/services/auth.service';

import { Post, Comment } from '../../../shared/models/models';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

  profile!: UserSummaryResponse & { isFollowing?: boolean; followerCount?: number };
  posts: Post[] = [];

  userId!: number;
  currentUserId: number = 0;

  loading = true;
  postsLoading = true;

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

      this.loadProfile();
      this.loadPosts();
    });
  }

  // ================= PROFILE =================

  loadProfile(): void {
    this.loading = true;
    // P3: getUserById returns UserSummaryResponse directly
    this.userService.getUserById(this.userId)
      .subscribe({
        next: (response) => {
          this.profile = { ...response, isFollowing: false, followerCount: 0 };
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  // ================= POSTS =================

  loadPosts(): void {
    this.postsLoading = true;
    // P3: getUserPosts returns FeedResponse directly
    this.postService.getUserPosts(this.userId, 0, 5)
      .subscribe({
        next: (response) => {
          this.posts = response.posts;

          // Load comments for each post
          this.posts.forEach(post => {
            this.loadComments(post);
          });

          this.postsLoading = false;
        },
        error: () => {
          this.postsLoading = false;
        }
      });
  }

  loadComments(post: Post): void {
    // P3: getComments returns Comment[] directly
    this.postService.getComments(post.id)
      .subscribe({
        next: (comments) => {
          post.comments = comments;
        },
        error: () => {
          post.comments = [];
        }
      });
  }

  // ================= FOLLOW =================

  toggleFollow(): void {
    if (this.profile.isFollowing) {
      this.networkService.unfollow(this.userId)
        .subscribe(() => {
          this.profile.isFollowing = false;
          if (this.profile.followerCount && this.profile.followerCount > 0) {
            this.profile.followerCount--;
          }
        });
    } else {
      this.networkService.follow(this.userId)
        .subscribe(() => {
          this.profile.isFollowing = true;
          this.profile.followerCount = (this.profile.followerCount || 0) + 1;
        });
    }
  }

  // ================= LIKE =================

  toggleLike(post: Post): void {
    if (post.likedByCurrentUser) {
      this.postService.unlikePost(post.id).subscribe(() => {
        post.likedByCurrentUser = false;
        if (post.likeCount > 0) post.likeCount--;
      });
    } else {
      this.postService.likePost(post.id).subscribe(() => {
        post.likedByCurrentUser = true;
        post.likeCount++;
      });
    }
  }

  // ================= COMMENT =================

  addComment(post: Post): void {
    if (!post.newComment || !post.newComment.trim()) return;

    const commentText = post.newComment;

    this.postService.addComment(post.id, commentText)
      .subscribe(() => {
        post.newComment = '';
        this.loadComments(post);
      });
  }
}
