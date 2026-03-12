import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { adminUserService } from '@/services/api/admin-user.service';
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

/**
 * Hook to fetch paginated list of users with filters
 */
export const useAdminUsers = (filters: UserFilters = {}) => {
  return useQuery<PaginatedResponse<AdminUser>>({
    queryKey: ['admin-users', filters],
    queryFn: () => adminUserService.listUsers(filters),
    staleTime: 30 * 1000, // 30 seconds
    gcTime: 5 * 60 * 1000, // 5 minutes (formerly cacheTime)
  });
};

/**
 * Hook to fetch detailed user information
 */
export const useUserDetails = (userId: number | null) => {
  return useQuery<AdminUserDetail>({
    queryKey: ['user-details', userId],
    queryFn: () => adminUserService.getUserDetails(userId!),
    enabled: userId !== null,
    staleTime: 60 * 1000, // 1 minute
    gcTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Hook to fetch user activity
 */
export const useUserActivity = (userId: number | null) => {
  return useQuery<UserActivity>({
    queryKey: ['user-activity', userId],
    queryFn: () => adminUserService.getUserActivity(userId!),
    enabled: userId !== null,
    staleTime: 60 * 1000, // 1 minute
    gcTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Hook to block a user
 */
export const useBlockUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ userId, request }: { userId: number; request: BlockUserRequest }) =>
      adminUserService.blockUser(userId, request),
    onSuccess: () => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      queryClient.invalidateQueries({ queryKey: ['user-details'] });
    },
  });
};

/**
 * Hook to suspend a user
 */
export const useSuspendUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ userId, request }: { userId: number; request: SuspendUserRequest }) =>
      adminUserService.suspendUser(userId, request),
    onSuccess: () => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      queryClient.invalidateQueries({ queryKey: ['user-details'] });
    },
  });
};

/**
 * Hook to activate a user
 */
export const useActivateUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userId: number) => adminUserService.activateUser(userId),
    onSuccess: () => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      queryClient.invalidateQueries({ queryKey: ['user-details'] });
    },
  });
};

/**
 * Hook to change user role
 */
export const useChangeUserRole = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ userId, request }: { userId: number; request: ChangeRoleRequest }) =>
      adminUserService.changeUserRole(userId, request),
    onSuccess: () => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      queryClient.invalidateQueries({ queryKey: ['user-details'] });
    },
  });
};
