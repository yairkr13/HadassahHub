import React from 'react';
import { Button } from '@/components/ui';
import { AdminUserDetail, UserRole, UserStatus } from '@/types/admin-user.types';
import { formatDate, formatDateTime } from '@/utils/dateUtils';

interface UserDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  user: AdminUserDetail | null;
  isLoading: boolean;
}

export const UserDetailsModal: React.FC<UserDetailsModalProps> = ({
  isOpen,
  onClose,
  user,
  isLoading,
}) => {
  if (!isOpen) return null;

  const getRoleBadgeColor = (role: UserRole): string => {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-800';
      case 'MODERATOR':
        return 'bg-blue-100 text-blue-800';
      case 'STUDENT':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusBadgeColor = (status: UserStatus): string => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'BLOCKED':
        return 'bg-red-100 text-red-800';
      case 'SUSPENDED':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-xl font-semibold text-text-primary">
              User Details
            </h3>
            <button
              onClick={onClose}
              className="text-text-secondary hover:text-text-primary transition-colors"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Loading State */}
          {isLoading && (
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
            </div>
          )}

          {/* User Details */}
          {!isLoading && user && (
            <div className="space-y-6">
              {/* Basic Info */}
              <div className="bg-gray-50 rounded-lg p-4">
                <h4 className="text-lg font-semibold text-text-primary mb-4">Basic Information</h4>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Full Name</p>
                    <p className="font-medium text-text-primary">{user.fullName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Email</p>
                    <p className="font-medium text-text-primary">{user.email}</p>
                  </div>
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Role</p>
                    <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${getRoleBadgeColor(user.role)}`}>
                      {user.role}
                    </span>
                  </div>
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Status</p>
                    <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeColor(user.status)}`}>
                      {user.status}
                    </span>
                  </div>
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Registration Date</p>
                    <p className="font-medium text-text-primary">{formatDate(user.registrationDate)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Last Login</p>
                    <p className="font-medium text-text-primary">
                      {user.lastLogin ? formatDateTime(user.lastLogin) : 'Never'}
                    </p>
                  </div>
                </div>
              </div>

              {/* Resource Statistics */}
              <div className="bg-gray-50 rounded-lg p-4">
                <h4 className="text-lg font-semibold text-text-primary mb-4">Resource Statistics</h4>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div className="bg-white rounded-lg p-3 text-center">
                    <p className="text-2xl font-bold text-primary">{user.resourcesUploaded}</p>
                    <p className="text-sm text-text-secondary mt-1">Total Uploaded</p>
                  </div>
                  <div className="bg-white rounded-lg p-3 text-center">
                    <p className="text-2xl font-bold text-green-600">{user.resourcesApproved}</p>
                    <p className="text-sm text-text-secondary mt-1">Approved</p>
                  </div>
                  <div className="bg-white rounded-lg p-3 text-center">
                    <p className="text-2xl font-bold text-yellow-600">{user.resourcesPending}</p>
                    <p className="text-sm text-text-secondary mt-1">Pending</p>
                  </div>
                  <div className="bg-white rounded-lg p-3 text-center">
                    <p className="text-2xl font-bold text-red-600">{user.resourcesRejected}</p>
                    <p className="text-sm text-text-secondary mt-1">Rejected</p>
                  </div>
                </div>
              </div>

              {/* Activity Information */}
              <div className="bg-gray-50 rounded-lg p-4">
                <h4 className="text-lg font-semibold text-text-primary mb-4">Activity</h4>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Total Downloads</p>
                    <p className="font-medium text-text-primary">{user.totalDownloads}</p>
                  </div>
                  <div>
                    <p className="text-sm text-text-secondary mb-1">Last Activity</p>
                    <p className="font-medium text-text-primary">
                      {user.lastActivity ? formatDateTime(user.lastActivity) : 'No activity'}
                    </p>
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="flex justify-end">
                <Button
                  variant="secondary"
                  onClick={onClose}
                >
                  Close
                </Button>
              </div>
            </div>
          )}

          {/* Error State */}
          {!isLoading && !user && (
            <div className="text-center py-12">
              <p className="text-text-secondary">Failed to load user details</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
