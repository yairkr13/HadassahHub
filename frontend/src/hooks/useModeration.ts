import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { moderationService } from '@/services/api/moderation.service';
import { RejectResourceRequest } from '@/types/moderation.types';

export const usePendingResources = () => {
  return useQuery({
    queryKey: ['pending-resources'],
    queryFn: moderationService.getPendingResources,
    staleTime: 30 * 1000, // 30 seconds - pending resources change frequently
    cacheTime: 2 * 60 * 1000, // 2 minutes
    refetchInterval: 60 * 1000, // Refetch every minute for admins
  });
};

export const useModerationStats = () => {
  return useQuery({
    queryKey: ['moderation-stats'],
    queryFn: moderationService.getModerationStats,
    staleTime: 2 * 60 * 1000, // 2 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useApproveResource = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => moderationService.approveResource(id),
    onSuccess: () => {
      // Invalidate and refetch relevant queries
      queryClient.invalidateQueries({ queryKey: ['pending-resources'] });
      queryClient.invalidateQueries({ queryKey: ['moderation-stats'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
    },
  });
};

export const useRejectResource = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: RejectResourceRequest }) => 
      moderationService.rejectResource(id, data),
    onSuccess: () => {
      // Invalidate and refetch relevant queries
      queryClient.invalidateQueries({ queryKey: ['pending-resources'] });
      queryClient.invalidateQueries({ queryKey: ['moderation-stats'] });
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
    },
  });
};

export const useResetResource = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => moderationService.resetResource(id),
    onSuccess: () => {
      // Invalidate and refetch relevant queries
      queryClient.invalidateQueries({ queryKey: ['pending-resources'] });
      queryClient.invalidateQueries({ queryKey: ['moderation-stats'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
    },
  });
};