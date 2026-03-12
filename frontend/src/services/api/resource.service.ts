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
    // Build FormData for both file uploads and URL resources
    // Backend expects multipart/form-data with @RequestParam
    const formData = new FormData();
    
    // Add file if present
    if (data.file) {
      formData.append('file', data.file);
    }
    
    // Add URL if present
    if (data.url) {
      formData.append('url', data.url);
    }
    
    // Add required fields
    formData.append('courseId', data.courseId.toString());
    formData.append('title', data.title);
    formData.append('type', data.type);
    
    // Add optional fields
    if (data.academicYear) {
      formData.append('academicYear', data.academicYear);
    }
    if (data.examTerm) {
      formData.append('examTerm', data.examTerm);
    }
    
    // Send multipart/form-data (browser sets Content-Type with boundary automatically)
    const response = await apiClient.post<Resource>('/resources', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
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

  // File download
  async downloadResource(id: number): Promise<void> {
    // First, get the resource metadata to get the filename
    let filename = `resource-${id}`;
    try {
      const resource = await this.getResourceById(id);
      if (resource.fileName) {
        filename = resource.fileName;
      }
    } catch (error) {
      console.warn('Could not fetch resource metadata, using default filename');
    }
    
    // Download the file
    const response = await apiClient.get(`/resources/${id}/download`, {
      responseType: 'blob',
    });
    
    // Try to extract filename from Content-Disposition header as fallback
    const contentDisposition = response.headers['content-disposition'];
    if (contentDisposition) {
      // Handle both quoted and unquoted filenames
      const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (filenameMatch && filenameMatch[1]) {
        filename = filenameMatch[1].replace(/['"]/g, '');
      }
    }
    
    // Create blob URL and trigger download
    const blob = new Blob([response.data]);
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};