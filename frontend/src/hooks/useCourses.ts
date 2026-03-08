import { useQuery } from '@tanstack/react-query';
import { courseService } from '@/services/api/course.service';
import { CourseFilters } from '@/types/course.types';

export const useCourses = (filters?: CourseFilters) => {
  return useQuery({
    queryKey: ['courses', filters],
    queryFn: () => courseService.getCourses(filters),
    staleTime: 10 * 60 * 1000, // 10 minutes - courses don't change frequently
    cacheTime: 30 * 60 * 1000, // 30 minutes - keep course list in cache longer
  });
};

export const useCourse = (id: number) => {
  return useQuery({
    queryKey: ['course', id],
    queryFn: () => courseService.getCourseById(id),
    enabled: !!id,
    staleTime: 15 * 60 * 1000, // 15 minutes - course details are stable
    cacheTime: 30 * 60 * 1000, // 30 minutes
  });
};

export const useCourseResources = (id: number) => {
  return useQuery({
    queryKey: ['course-resources', id],
    queryFn: () => courseService.getCourseResources(id),
    enabled: !!id,
    staleTime: 2 * 60 * 1000, // 2 minutes - resources change more frequently
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useCourseResourceStats = (id: number) => {
  return useQuery({
    queryKey: ['course-resource-stats', id],
    queryFn: () => courseService.getCourseResourceStats(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - stats update with new resources
    cacheTime: 15 * 60 * 1000, // 15 minutes
  });
};