import { apiClient } from './client';
import { Course, CourseFilters, mapDisplayCategoryToBackend } from '@/types/course.types';

export const courseService = {
  async getCourses(filters?: CourseFilters): Promise<Course[]> {
    const params = new URLSearchParams();
    
    if (filters?.category) {
      // Map frontend display category to backend category
      const backendCategory = mapDisplayCategoryToBackend(filters.category);
      params.append('category', backendCategory);
    }
    
    if (filters?.year) {
      params.append('year', filters.year.toString());
    }
    
    if (filters?.search) {
      params.append('search', filters.search);
    }
    
    const queryString = params.toString();
    const url = queryString ? `/courses?${queryString}` : '/courses';
    
    const response = await apiClient.get<Course[]>(url);
    return response.data;
  },

  async getCourseById(id: number): Promise<Course> {
    const response = await apiClient.get<Course>(`/courses/${id}`);
    return response.data;
  },

  async getCourseResources(id: number): Promise<any[]> {
    const response = await apiClient.get<any[]>(`/courses/${id}/resources`);
    return response.data;
  },

  async getCourseResourceStats(id: number): Promise<any> {
    const response = await apiClient.get<any>(`/courses/${id}/resources/stats`);
    return response.data;
  },
};