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

  async approveResource(id: number): Promise<void> {
    await apiClient.post(`/admin/resources/${id}/approve`);
  },

  async rejectResource(id: number, data: RejectResourceRequest): Promise<void> {
    await apiClient.post(`/admin/resources/${id}/reject`, data);
  },

  async resetResource(id: number): Promise<void> {
    await apiClient.post(`/admin/resources/${id}/reset`);
  },
};