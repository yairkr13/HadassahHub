import { apiClient } from './client';
import { 
  Resource, 
  CreateResourceRequest, 
  UpdateResourceRequest, 
  ResourceStats,
  ResourceFilters 
} from '@/types/resource.types';

export const resourceService = {
  // Student resource management
  async createResource(data: CreateResourceRequest): Promise<Resource> {
    const response = await apiClient.post<Resource>('/resources', data);
    return response.data;
  },

  async getMyResources(filters?: ResourceFilters): Promise<Resource[]> {
    const params = new URLSearchParams();
    
    if (filters?.type) {
      params.append('type', filters.type);
    }
    if (filters?.status) {
      params.append('status', filters.status);
    }
    if (filters?.search) {
      params.append('search', filters.search);
    }
    
    const queryString = params.toString();
    const url = queryString ? `/resources/my?${queryString}` : '/resources/my';
    
    const response = await apiClient.get<Resource[]>(url);
    return response.data;
  },

  async getResourceById(id: number): Promise<Resource> {
    const response = await apiClient.get<Resource>(`/resources/${id}`);
    return response.data;
  },

  async updateResource(id: number, data: UpdateResourceRequest): Promise<Resource> {
    const response = await apiClient.put<Resource>(`/resources/${id}`, data);
    return response.data;
  },

  async deleteResource(id: number): Promise<void> {
    await apiClient.delete(`/resources/${id}`);
  },

  // Course resources
  async getCourseResources(courseId: number, filters?: ResourceFilters): Promise<Resource[]> {
    const params = new URLSearchParams();
    
    if (filters?.type) {
      params.append('type', filters.type);
    }
    if (filters?.academicYear) {
      params.append('academicYear', filters.academicYear);
    }
    if (filters?.search) {
      params.append('search', filters.search);
    }
    
    const queryString = params.toString();
    const url = queryString 
      ? `/courses/${courseId}/resources?${queryString}` 
      : `/courses/${courseId}/resources`;
    
    const response = await apiClient.get<Resource[]>(url);
    return response.data;
  },

  async getCourseResourceStats(courseId: number): Promise<ResourceStats> {
    const response = await apiClient.get<ResourceStats>(`/courses/${courseId}/resources/stats`);
    return response.data;
  },
};