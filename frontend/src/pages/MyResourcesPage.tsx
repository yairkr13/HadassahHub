import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Button, Card } from '@/components/ui';
import { ResourceList } from '@/components/resources';
import { EmptyState, ErrorState } from '@/components/common';
import { resourceService } from '@/services/api/resource.service';
import { ResourceFilters, Resource } from '@/types/resource.types';

export const MyResourcesPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<ResourceFilters>({});

  // Fetch user's resources
  const { 
    data: resources = [], 
    isLoading, 
    error,
    refetch
  } = useQuery({
    queryKey: ['my-resources', filters],
    queryFn: () => resourceService.getMyResources(filters),
  });

  // Delete resource mutation
  const deleteMutation = useMutation({
    mutationFn: resourceService.deleteResource,
    onSuccess: () => {
      // Invalidate and refetch resources
      queryClient.invalidateQueries({ queryKey: ['my-resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });
    },
    onError: (error: any) => {
      console.error('Failed to delete resource:', error);
      // You could add a toast notification here
    },
  });

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this resource?')) {
      deleteMutation.mutate(id);
    }
  };

  const handleEdit = (resource: Resource) => {
    // For now, we'll just navigate to upload page
    // In a full implementation, you might want to pre-populate the form
    console.log('Edit resource:', resource);
    // navigate(`/upload?edit=${resource.id}`);
  };

  const handleFiltersChange = (newFilters: ResourceFilters) => {
    setFilters(newFilters);
  };

  if (error) {
    return (
      <div className="min-h-screen bg-background-soft">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-text-primary mb-2">
              My Resources
            </h1>
            <p className="text-text-secondary">
              Manage your uploaded study materials
            </p>
          </div>
          
          <ErrorState
            title="Error Loading Resources"
            description={error instanceof Error ? error.message : 'Failed to load your resources. Please try again.'}
            onRetry={() => refetch()}
          />
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            My Resources
          </h1>
          <p className="text-text-secondary">
            Manage your uploaded study materials. To upload new resources, visit a course page.
          </p>
        </div>

        {/* Resource Statistics */}
        {!isLoading && resources.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Total Resources</p>
                  <p className="text-2xl font-bold text-text-primary">{resources.length}</p>
                </div>
              </div>
            </Card>

            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Approved</p>
                  <p className="text-2xl font-bold text-text-primary">
                    {resources.filter(r => r.status === 'APPROVED').length}
                  </p>
                </div>
              </div>
            </Card>

            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-yellow-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Pending</p>
                  <p className="text-2xl font-bold text-text-primary">
                    {resources.filter(r => r.status === 'PENDING').length}
                  </p>
                </div>
              </div>
            </Card>

            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-red-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Rejected</p>
                  <p className="text-2xl font-bold text-text-primary">
                    {resources.filter(r => r.status === 'REJECTED').length}
                  </p>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* Resource List */}
        <ResourceList
          resources={resources}
          isLoading={isLoading}
          showCourse={true}
          showActions={true}
          showFilters={true}
          filters={filters}
          onFiltersChange={handleFiltersChange}
          onDelete={handleDelete}
          onEdit={handleEdit}
          emptyMessage="You haven't uploaded any resources yet"
        />

        {/* Empty state with upload prompt */}
        {!isLoading && resources.length === 0 && Object.keys(filters).length === 0 && (
          <EmptyState
            icon={
              <svg className="w-12 h-12 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
            }
            title="Start Sharing Resources"
            description="You haven't uploaded any resources yet. Visit a course page to upload your first study material and help fellow students."
            action={{
              label: "Browse Courses",
              onClick: () => window.location.href = "/courses",
              variant: "primary"
            }}
          />
        )}
      </div>
    </div>
  );
};