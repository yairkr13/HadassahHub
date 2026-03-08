import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { resourceService } from '@/services/api/resource.service';
import { ResourceFilters, CreateResourceRequest, UpdateResourceRequest } from '@/types/resource.types';

export const useMyResources = (filters?: ResourceFilters) => {
  return useQuery({
    queryKey: ['my-resources', filters],
    queryFn: () => resourceService.getMyResources(filters),
    staleTime: 1 * 60 * 1000, // 1 minute - user's own resources change frequently
    cacheTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useCourseResources = (courseId: number, filters?: ResourceFilters) => {
  return useQuery({
    queryKey: ['course-resources', courseId, filters],
    queryFn: () => resourceService.getCourseResources(courseId, filters),
    enabled: !!courseId,
    staleTime: 2 * 60 * 1000, // 2 minutes - resources change frequently
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useCourseResourceStats = (courseId: number) => {
  return useQuery({
    queryKey: ['course-resource-stats', courseId],
    queryFn: () => resourceService.getCourseResourceStats(courseId),
    enabled: !!courseId,
    staleTime: 5 * 60 * 1000, // 5 minutes - stats update with new resources
    cacheTime: 15 * 60 * 1000, // 15 minutes
  });
};

export const useResource = (id: number) => {
  return useQuery({
    queryKey: ['resource', id],
    queryFn: () => resourceService.getResourceById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useCreateResource = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateResourceRequest) => resourceService.createResource(data),
    onSuccess: () => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resource-stats'] });
    },
  });
};

export const useUpdateResource = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateResourceRequest }) => 
      resourceService.updateResource(id, data),
    onSuccess: (_, { id }) => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['resource', id] });
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });
    },
  });
};

export const useDeleteResource = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => resourceService.deleteResource(id),
    onSuccess: () => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resource-stats'] });
    },
  });
};