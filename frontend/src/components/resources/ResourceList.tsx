import React, { useState, useMemo } from 'react';
import { Resource, ResourceFilters } from '@/types/resource.types';
import { ResourceCard } from './ResourceCard';
import { ResourceFiltersComponent } from './ResourceFilters';
import { SkeletonGrid, EmptyState } from '@/components/common';

interface ResourceListProps {
  resources: Resource[];
  isLoading?: boolean;
  showCourse?: boolean;
  showActions?: boolean;
  showFilters?: boolean;
  filters?: ResourceFilters;
  onFiltersChange?: (filters: ResourceFilters) => void;
  onDelete?: (id: number) => void;
  onEdit?: (resource: Resource) => void;
  emptyMessage?: string;
  showModerationActions?: boolean;
  onModerationSuccess?: () => void;
}

export const ResourceList: React.FC<ResourceListProps> = ({
  resources,
  isLoading = false,
  showCourse = false,
  showActions = false,
  showFilters = false,
  filters = {},
  onFiltersChange,
  onDelete,
  onEdit,
  emptyMessage = 'No resources found',
  showModerationActions = false,
  onModerationSuccess,
}) => {
  const [localFilters, setLocalFilters] = useState<ResourceFilters>(filters);

  const handleFiltersChange = (newFilters: ResourceFilters) => {
    setLocalFilters(newFilters);
    onFiltersChange?.(newFilters);
  };

  // Memoize expensive computations
  const { validResources, hasActiveFilters } = useMemo(() => {
    // Filter out invalid resources
    const valid = resources.filter((resource) => {
      return resource && resource.id && typeof resource.id === 'number';
    });

    // Check for active filters
    const hasFilters = Object.keys(localFilters).some(key => 
      localFilters[key as keyof ResourceFilters] !== undefined && 
      localFilters[key as keyof ResourceFilters] !== ''
    );

    return { validResources: valid, hasActiveFilters: hasFilters };
  }, [resources, localFilters]);

  if (isLoading) {
    return (
      <div>
        {showFilters && (
          <ResourceFiltersComponent
            filters={localFilters}
            onFiltersChange={handleFiltersChange}
          />
        )}
        <div className="mb-6">
          <div className="animate-pulse bg-gray-200 rounded h-4 w-32"></div>
        </div>
        <SkeletonGrid count={6} />
      </div>
    );
  }

  return (
    <div>
      {/* Filters */}
      {showFilters && (
        <ResourceFiltersComponent
          filters={localFilters}
          onFiltersChange={handleFiltersChange}
        />
      )}

      {/* Results count */}
      {validResources.length > 0 && (
        <div className="mb-6">
          <p className="text-text-secondary">
            {validResources.length} {validResources.length === 1 ? 'resource' : 'resources'} found
          </p>
        </div>
      )}

      {/* Resource grid */}
      {validResources.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {validResources.map((resource) => (
            <ResourceCard
              key={resource.id}
              resource={resource}
              showCourse={showCourse}
              showActions={showActions}
              showModerationActions={showModerationActions}
              onDelete={onDelete}
              onEdit={onEdit}
              onModerationSuccess={onModerationSuccess}
            />
          ))}
        </div>
      ) : (
        <EmptyState
          icon={
            <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          }
          title={hasActiveFilters ? "No resources match your filters" : emptyMessage}
          description={
            hasActiveFilters 
              ? "Try adjusting your search criteria or clearing some filters to see more results."
              : showFilters 
                ? "No resources have been uploaded yet. Be the first to share study materials!"
                : "No resources have been uploaded yet."
          }
          action={hasActiveFilters ? {
            label: "Clear Filters",
            onClick: () => handleFiltersChange({}),
            variant: "secondary"
          } : undefined}
        />
      )}
    </div>
  );
};