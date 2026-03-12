import React from 'react';
import { Resource, getResourceTypeDisplayName, getResourceTypeColor } from '@/types/resource.types';
import { Card } from '@/components/ui';
import { SkeletonCard, EmptyState } from '@/components/common';
import { ModerationActions } from './ModerationActions';

interface PendingResourcesListProps {
  resources: Resource[];
  isLoading?: boolean;
  onApprove: (id: number) => void;
  onReject: (id: number, reason: string) => void;
  onReset: (id: number) => void;
  moderationLoading?: boolean;
}

export const PendingResourcesList: React.FC<PendingResourcesListProps> = ({
  resources,
  isLoading = false,
  onApprove,
  onReject,
  onReset,
  moderationLoading = false,
}) => {
  const formatDate = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch (error) {
      return 'Unknown date';
    }
  };

  const handleResourceClick = (url: string | null) => {
    if (url) {
      window.open(url, '_blank', 'noopener,noreferrer');
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 3 }).map((_, index) => (
          <SkeletonCard key={index} />
        ))}
      </div>
    );
  }

  if (resources.length === 0) {
    return (
      <EmptyState
        icon={
          <svg className="w-12 h-12 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
          </svg>
        }
        title="All Caught Up!"
        description="There are no pending resources to moderate at the moment. Great job keeping up with submissions!"
      />
    );
  }

  return (
    <div className="space-y-4">
      {resources.map((resource) => {
        const typeDisplayName = getResourceTypeDisplayName(resource.type);
        const typeColor = getResourceTypeColor(resource.type);
        const uploaderName = resource.uploaderName || 'Unknown User';
        const courseName = resource.courseName || 'Unknown Course';

        return (
          <Card key={resource.id} hover className="group">
            <div className="p-6">
              {/* Header */}
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  {/* Title and Type Badge */}
                  <div className="flex items-center gap-3 mb-3">
                    <h3 
                      className={`text-lg font-semibold text-text-primary line-clamp-1 group-hover:text-primary transition-colors ${
                        resource.url ? 'cursor-pointer' : ''
                      }`}
                      onClick={() => handleResourceClick(resource.url)}
                      title={resource.title}
                    >
                      {resource.title}
                    </h3>
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium transition-colors ${typeColor}`}>
                      {typeDisplayName}
                    </span>
                  </div>

                  {/* Course and Uploader Info */}
                  <div className="flex items-center gap-6 text-sm text-text-secondary mb-4">
                    <span className="flex items-center gap-1">
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                      <strong>Course:</strong> {courseName}
                    </span>
                    <span className="flex items-center gap-1">
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                      <strong>Uploader:</strong> {uploaderName}
                    </span>
                  </div>

                  {/* Additional Info */}
                  <div className="flex items-center gap-6 text-sm text-text-secondary">
                    {resource.academicYear && (
                      <span className="bg-gray-50 px-2 py-1 rounded text-xs font-medium">
                        {resource.academicYear}
                      </span>
                    )}
                    {resource.examTerm && (
                      <span className="bg-gray-50 px-2 py-1 rounded text-xs font-medium">
                        {resource.examTerm}
                      </span>
                    )}
                    {resource.createdAt && (
                      <span className="text-xs">
                        <strong>Uploaded:</strong> {formatDate(resource.createdAt)}
                      </span>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="ml-6 flex-shrink-0">
                  <ModerationActions
                    resource={resource}
                    onApprove={onApprove}
                    onReject={onReject}
                    onReset={onReset}
                    isLoading={moderationLoading}
                  />
                </div>
              </div>

              {/* Resource URL Preview */}
              {resource.url && (
                <div className="mt-4 p-4 bg-gray-50 border border-gray-200 rounded-lg group-hover:bg-gray-100 transition-colors">
                  <p className="text-sm text-text-secondary mb-2 font-medium">Resource URL:</p>
                  <button
                    onClick={() => handleResourceClick(resource.url)}
                    className="text-sm text-primary hover:text-primary-dark font-medium break-all hover:underline transition-colors"
                  >
                    {resource.url}
                  </button>
                </div>
              )}
            </div>
          </Card>
        );
      })}
    </div>
  );
};