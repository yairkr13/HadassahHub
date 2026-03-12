import { apiClient } from './client';
import { Resource } from '@/types/resource.types';
import { ModerationStats, RejectResourceRequest } from '@/types/moderation.types';

export const moderationService = {
  async getPendingResources(): Promise<Resource[]> {
    const response = await apiClient.get<Resource[]>('/admin/resources/pending');
    return response.data;
  },

  async getModerationStats(): Promise<ModerationStats> {
    const response = await apiClient.get<ModerationStats>('/admin/resources/stats');
    return response.data;
  },

  async approveResource(id: number): Promise<Resource> {
    const response = await apiClient.put<Resource>(`/admin/resources/${id}/approve`);
    return response.data;
  },

  async rejectResource(id: number, data: RejectResourceRequest): Promise<Resource> {
    const response = await apiClient.put<Resource>(`/admin/resources/${id}/reject`, data);
    return response.data;
  },

  async resetResource(id: number): Promise<Resource> {
    const response = await apiClient.put<Resource>(`/admin/resources/${id}/reset`);
    return response.data;
  },
};