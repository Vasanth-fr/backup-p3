// ─── User Models ───────────────────────────────────────────────────────

export type UserRole = 'PERSONAL' | 'CREATOR' | 'BUSINESS';
export type PrivacyType = 'PUBLIC' | 'PRIVATE';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  bio?: string;
  profilePicture?: string;
  location?: string;
  website?: string;
  role: UserRole;
  privacy: PrivacyType;
  businessName?: string;
  category?: string;
  contactEmail?: string;
  contactPhone?: string;
  businessAddress?: string;
  businessHours?: string;
  followerCount: number;
  followingCount: number;
  connectionCount: number;
  postCount: number;
  isFollowing: boolean;
  isConnected: boolean;
  createdAt: string;
}

// ─── User Summary (P3 user-service response) ────────────────────────────

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

// ─── Auth Models ────────────────────────────────────────────────────────

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName?: string;
  username: string;
  accountType: UserRole;

}

export interface LoginRequest {
  identifier: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
}

// ─── Post Models ────────────────────────────────────────────────────────

export type PostType = 'TEXT' | 'IMAGE' | 'PROMOTIONAL' | 'REPOST' | 'ANNOUNCEMENT';

export interface Post {
  id: number;
  author: User;
  content: string;
  hashtags?: string;
  imageUrl?: string;
  type: PostType;
  callToActionLabel?: string;
  callToActionUrl?: string;
  pinned: boolean;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  likedByCurrentUser: boolean;
  originalPost?: Post;
  createdAt: string;
  updatedAt: string;
  comments?: Comment[];
  newComment?: string;
  showComments?: boolean;
}

export interface CreatePostRequest {
  content: string;
  hashtags?: string;
  imageUrl?: string;
  type?: PostType;
  callToActionLabel?: string;
  callToActionUrl?: string;
  scheduledAt?: string;
  originalPostId?: number;
}

// ─── Comment Models ─────────────────────────────────────────────────────

export interface Comment {
  id: number;
  content: string;
  username: string;
  userId: number;
  postId: number;
  likeCount: number;
  likedByCurrentUser: boolean;
  parentId?: number;
  replies?: Comment[];
  createdAt: string;
}

// ─── Notification Models ────────────────────────────────────────────────

export type NotificationType =
  | 'CONNECTION_REQUEST'
  | 'CONNECTION_ACCEPTED'
  | 'NEW_FOLLOWER'
  | 'POST_LIKED'
  | 'POST_COMMENTED'
  | 'POST_SHARED';

export interface Notification {
  id: number;
  recipientId: number;
  senderId?: number;
  sender?: { username?: string };
  type: NotificationType;
  referenceId?: number;
  message: string;
  read: boolean;
  createdAt: string;
}

// ─── Connection Models ──────────────────────────────────────────────────

export type ConnectionStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

export interface Connection {
  id: number;
  requesterId: number;
  requesterUsername: string;
  requesterFullName: string;
  addresseeId: number;
  addresseeUsername: string;
  addresseeFullName: string;
  status: string;
}

// ─── API Response Wrapper ───────────────────────────────────────────────

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  statusCode: number;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ─── Feed Response (P3 post-service) ────────────────────────────────────

export interface FeedResponse {
  posts: Post[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}

// ─── Analytics ──────────────────────────────────────────────────────────

export interface PostAnalytics {
  postId: number;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  engagementRate: number;
}

// ─── Follow ─────────────────────────────────────────────────────────────

export interface Follow {
  id: number;
  followerId: number;
  followingId: number;
}
