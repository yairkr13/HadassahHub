import { useQuery } from '@tanstack/react-query';
import { courseService } from '@/services/api/course.service';
import { Course, CourseFilters } from '@/types/course.types';

export const useCourses = (filters?: CourseFilters) => {
  return useQuery<Course[]>({
    queryKey: ['courses', filters],
    queryFn: () => courseService.getCourses(filters),
    staleTime: 10 * 60 * 1000, // 10 minutes - courses don't change frequently
    gcTime: 30 * 60 * 1000, // 30 minutes - keep course list in cache longer
  });
};

export const useCourse = (id: number) => {
  return useQuery<Course>({
    queryKey: ['course', id],
    queryFn: () => courseService.getCourseById(id),
    enabled: !!id,
    staleTime: 15 * 60 * 1000, // 15 minutes - course details are stable
    gcTime: 30 * 60 * 1000, // 30 minutes
  });
};

export const useCourseResources = (id: number) => {
  return useQuery<any[]>({
    queryKey: ['course-resources', id],
    queryFn: () => courseService.getCourseResources(id),
    enabled: !!id,
    staleTime: 2 * 60 * 1000, // 2 minutes - resources change more frequently
    gcTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useCourseResourceStats = (id: number) => {
  return useQuery<any>({
    queryKey: ['course-resource-stats', id],
    queryFn: () => courseService.getCourseResourceStats(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes - stats update with new resources
    gcTime: 15 * 60 * 1000, // 15 minutes
  });
};