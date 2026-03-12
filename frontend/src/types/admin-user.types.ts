/**
 * TypeScript types for Admin User Management
 */

export type UserRole = 'STUDENT' | 'MODERATOR' | 'ADMIN';
export type UserStatus = 'ACTIVE' | 'BLOCKED' | 'SUSPENDED';

/**
 * Admin user DTO for list view
 */
export interface AdminUser {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  registrationDate: string;
  lastLogin: string | null;
  resourcesUploaded: number;
  emailVerified: boolean;
}

/**
 * Detailed admin user DTO
 */
export interface AdminUserDetail {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  registrationDate: string;
  lastLogin: string | null;
  emailVerified: boolean;
  resourcesUploaded: number;
  resourcesApproved: number;
  resourcesPending: number;
  resourcesRejected: number;
  totalDownloads: number;
  lastActivity: string | null;
}

/**
 * User filter DTO
 */
export interface UserFilters {
  search?: string;
  role?: UserRole;
  status?: UserStatus;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

/**
 * Block user request
 */
export interface BlockUserRequest {
  reason: string;
}

/**
 * Suspend user request
 */
export interface SuspendUserRequest {
  reason: string;
  expiresAt: string; // ISO date-time string
}

/**
 * Change role request
 */
export interface ChangeRoleRequest {
  newRole: UserRole;
  reason?: string;
}

/**
 * User activity DTO
 */
export interface UserActivity {
  userId: number;
  resourcesUploaded: UserResource[];
  recentDownloads: UserDownload[];
  totalUploads: number;
  totalDownloads: number;
}

export interface UserResource {
  id: number;
  title: string;
  type: string;
  status: string;
  uploadedAt: string;
}

export interface UserDownload {
  resourceId: number;
  resourceTitle: string;
  downloadedAt: string;
}
