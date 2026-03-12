import React from 'react';
import {
  Resource,
  getResourceTypeDisplayName,
  getResourceStatusDisplayName,
  getResourceTypeColor,
  getResourceStatusColor
} from '@/types/resource.types';
import { Card } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { resourceService } from '@/services/api/resource.service';
import { ModerationActions } from './ModerationActions';

interface ResourceCardProps {
  resource: Resource;
  showCourse?: boolean;
  showActions?: boolean;
  showModerationActions?: boolean;
  onDelete?: (id: number) => void;
  onEdit?: (resource: Resource) => void;
  onModerationSuccess?: () => void;
}

const ResourceCardComponent: React.FC<ResourceCardProps> = ({
  resource,
  showCourse = false,
  showActions = false,
  showModerationActions = false,
  onDelete,
  onEdit,
  onModerationSuccess,
}) => {
  const { user } = useAuth();

  // Defensive checks for resource data
  if (!resource || !resource.id) {
    console.warn('ResourceCard: Invalid resource data', resource);
    return null;
  }

  // Safe access to flat properties with fallbacks
  const uploaderName = resource.uploaderName || 'Unknown User';
  const courseName = resource.courseName || 'Unknown Course';
  const uploadedById = resource.uploadedById || 0;

  const isOwner = resource.isOwner || (user?.id === uploadedById);
  const isFileResource = resource.isFileUpload || false;
  const canModerate = user?.role === 'ADMIN' || user?.role === 'MODERATOR';
  const isPending = resource.status === 'PENDING';

  const typeDisplayName = getResourceTypeDisplayName(resource.type);
  const statusDisplayName = getResourceStatusDisplayName(resource.status);
  const typeColor = getResourceTypeColor(resource.type);
  const statusColor = getResourceStatusColor(resource.status);

  const handleResourceClick = async () => {
    if (isFileResource) {
      // Download file
      try {
        await resourceService.downloadResource(resource.id);
      } catch (error) {
        console.error('Error downloading file:', error);
      }
    } else if (resource.url) {
      // Open URL in new tab
      window.open(resource.url, '_blank', 'noopener,noreferrer');
    }
  };

  const formatDate = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      });
    } catch (error) {
      return 'Unknown date';
    }
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  return (
    <Card hover className="group">
      <div className="p-6">
        {/* Header with badges */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center gap-2 flex-wrap">
            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium transition-colors ${typeColor}`}>
              {typeDisplayName}
            </span>
            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium transition-colors ${statusColor}`}>
              {statusDisplayName}
            </span>
            {isFileResource && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                <svg className="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                File
              </span>
            )}
          </div>

          {showActions && isOwner && (
            <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
              {onEdit && (
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onEdit(resource);
                  }}
                  className="p-2 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-lg transition-all duration-200"
                  title="Edit resource"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
              )}
              {onDelete && (
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onDelete(resource.id);
                  }}
                  className="p-2 text-text-secondary hover:text-red-600 hover:bg-red-50 rounded-lg transition-all duration-200"
                  title="Delete resource"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              )}
            </div>
          )}
        </div>

        {/* Resource title */}
        <h3
          className={`text-lg font-semibold text-text-primary mb-3 line-clamp-2 leading-snug ${(resource.url || isFileResource) ? 'cursor-pointer hover:text-primary transition-colors group-hover:text-primary' : ''
            }`}
          onClick={handleResourceClick}
        >
          {resource.title || 'Untitled Resource'}
        </h3>

        {/* Course name (if showing course) */}
        {showCourse && (
          <p className="text-sm text-text-secondary mb-3 font-medium">
            <span className="text-gray-500">Course:</span> {courseName}
          </p>
        )}

        {/* File metadata (if file resource) */}
        {isFileResource && resource.fileName && (
          <div className="flex items-center gap-3 text-sm text-text-secondary mb-3 bg-gray-50 p-2 rounded">
            <svg className="w-5 h-5 text-primary flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <div className="flex-1 min-w-0">
              <p className="font-medium truncate">{resource.fileName}</p>
              {resource.fileSize && (
                <p className="text-xs">{formatFileSize(resource.fileSize)}</p>
              )}
            </div>
          </div>
        )}

        {/* Academic year and exam term */}
        {(resource.academicYear || resource.examTerm) && (
          <div className="flex items-center gap-4 text-sm text-text-secondary mb-4">
            {resource.academicYear && (
              <span className="bg-gray-50 px-2 py-1 rounded text-xs">
                {resource.academicYear}
              </span>
            )}
            {resource.examTerm && (
              <span className="bg-gray-50 px-2 py-1 rounded text-xs">
                {resource.examTerm}
              </span>
            )}
          </div>
        )}

        {/* Footer */}
        <div className="flex items-center justify-between text-sm text-text-secondary mt-auto">
          <div className="flex flex-col gap-1">
            <span className="font-medium">{uploaderName}</span>
            {resource.createdAt && (
              <span className="text-xs">{formatDate(resource.createdAt)}</span>
            )}
          </div>

          {/* Action button (download or open link) */}
          {(resource.url || isFileResource) && (
            <button
              onClick={handleResourceClick}
              className="flex items-center justify-center w-8 h-8 text-primary hover:text-primary-dark hover:bg-primary/10 rounded-lg transition-all duration-200 group-hover:bg-primary/10"
              title={isFileResource ? 'Download file' : 'Open resource'}
            >
              {isFileResource ? (
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
              ) : (
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                </svg>
              )}
            </button>
          )}
        </div>

        {/* Rejection reason (if rejected) */}
        {resource.status === 'REJECTED' && resource.rejectionReason && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-sm text-red-800">
              <strong>Rejection Reason:</strong> {resource.rejectionReason}
            </p>
          </div>
        )}

        {/* Owner feedback for pending resources */}
        {isOwner && isPending && (
          <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
            <p className="text-sm text-yellow-800">
              <strong>⏳ Your upload is waiting for admin approval</strong>
            </p>
          </div>
        )}

        {/* Moderation actions for admins/moderators */}
        {showModerationActions && canModerate && isPending && (
          <div className="mt-4 pt-4 border-t border-gray-200">
            <ModerationActions
              resourceId={resource.id}
              resourceTitle={resource.title}
              onSuccess={onModerationSuccess}
            />
          </div>
        )}
      </div>
    </Card>
  );
};

// Memoize the component to prevent unnecessary re-renders
export const ResourceCard = React.memo(ResourceCardComponent);