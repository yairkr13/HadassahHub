import { apiClient } from './client';
import {
  AdminUser,
  AdminUserDetail,
  UserFilters,
  BlockUserRequest,
  SuspendUserRequest,
  ChangeRoleRequest,
  UserActivity,
} from '@/types/admin-user.types';
import { PaginatedResponse } from '@/types/api.types';

export const adminUserService = {
  /**
   * List users with optional filters and pagination
   */
  async listUsers(filters: UserFilters = {}): Promise<PaginatedResponse<AdminUser>> {
    const params = new URLSearchParams();
    
    if (filters.search) params.append('search', filters.search);
    if (filters.role) params.append('role', filters.role);
    if (filters.status) params.append('status', filters.status);
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size !== undefined) params.append('size', filters.size.toString());
    if (filters.sortBy) params.append('sortBy', filters.sortBy);
    if (filters.sortDirection) params.append('sortDirection', filters.sortDirection);

    const response = await apiClient.get<PaginatedResponse<AdminUser>>(
      `/admin/users?${params.toString()}`
    );
    return response.data;
  },

  /**
   * Get detailed information about a specific user
   */
  async getUserDetails(userId: number): Promise<AdminUserDetail> {
    const response = await apiClient.get<AdminUserDetail>(`/admin/users/${userId}`);
    return response.data;
  },

  /**
   * Block a user permanently
   */
  async blockUser(userId: number, request: BlockUserRequest): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/block`, request);
  },

  /**
   * Suspend a user temporarily
   */
  async suspendUser(userId: number, request: SuspendUserRequest): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/suspend`, request);
  },

  /**
   * Activate a blocked or suspended user
   */
  async activateUser(userId: number): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/activate`);
  },

  /**
   * Change a user's role
   */
  async changeUserRole(userId: number, request: ChangeRoleRequest): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/role`, request);
  },

  /**
   * Get user activity history
   */
  async getUserActivity(userId: number): Promise<UserActivity> {
    const response = await apiClient.get<UserActivity>(`/admin/users/${userId}/activity`);
    return response.data;
  },
};
