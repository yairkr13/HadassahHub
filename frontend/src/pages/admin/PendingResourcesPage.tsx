import React from 'react';
import { usePendingResources, useModerationStats } from '@/hooks/useModeration';
import { ResourceCard } from '@/components/resources/ResourceCard';
import { Card } from '@/components/ui';
import { Resource } from '@/types/resource.types';

export const PendingResourcesPage: React.FC = () => {
  const { data: pendingResources, isLoading, refetch } = usePendingResources();
  const { data: stats } = useModerationStats();

  const resources = (pendingResources ?? []) as Resource[];
  const moderationStats = (stats ?? { pendingCount: 0, approvedCount: 0, rejectedCount: 0 }) as { pendingCount: number; approvedCount: number; rejectedCount: number };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background-soft flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Resource Moderation
          </h1>
          <p className="text-text-secondary">
            Review and approve pending resource uploads
          </p>
        </div>

        {/* Statistics Cards */}
        {moderationStats && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <Card>
              <div className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-text-secondary">Pending Review</p>
                    <p className="text-3xl font-bold text-yellow-600 mt-2">{moderationStats.pendingCount}</p>
                  </div>
                  <div className="p-3 bg-yellow-100 rounded-full">
                    <svg className="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
              </div>
            </Card>

            <Card>
              <div className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-text-secondary">Approved</p>
                    <p className="text-3xl font-bold text-green-600 mt-2">{moderationStats.approvedCount}</p>
                  </div>
                  <div className="p-3 bg-green-100 rounded-full">
                    <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
              </div>
            </Card>

            <Card>
              <div className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-text-secondary">Rejected</p>
                    <p className="text-3xl font-bold text-red-600 mt-2">{moderationStats.rejectedCount}</p>
                  </div>
                  <div className="p-3 bg-red-100 rounded-full">
                    <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* Pending Resources List */}
        <div className="mb-6">
          <h2 className="text-xl font-semibold text-text-primary mb-4">
            Pending Resources ({resources.length})
          </h2>
        </div>

        {resources.length > 0 ? (
          <div className="grid grid-cols-1 gap-6">
            {resources.map((resource: Resource) => (
              <ResourceCard
                key={resource.id}
                resource={resource}
                showCourse={true}
                showModerationActions={true}
                onModerationSuccess={() => refetch()}
              />
            ))}
          </div>
        ) : (
          <Card>
            <div className="p-12 text-center">
              <svg
                className="w-16 h-16 text-gray-400 mx-auto mb-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <h3 className="text-lg font-medium text-text-primary mb-2">
                No Pending Resources
              </h3>
              <p className="text-text-secondary">
                All resources have been reviewed. Great job!
              </p>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
};
