import React, { useState } from 'react';
import { Button } from '@/components/ui';
import { AdminUser, UserRole } from '@/types/admin-user.types';

interface ChangeRoleModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (newRole: UserRole, reason?: string) => void;
  user: AdminUser | null;
  isLoading?: boolean;
}

export const ChangeRoleModal: React.FC<ChangeRoleModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  user,
  isLoading = false,
}) => {
  const [newRole, setNewRole] = useState<UserRole>('STUDENT');
  const [reason, setReason] = useState('');
  const [error, setError] = useState('');

  React.useEffect(() => {
    if (isOpen && user) {
      // Set default to a different role than current
      if (user.role === 'STUDENT') setNewRole('MODERATOR');
      else if (user.role === 'MODERATOR') setNewRole('STUDENT');
      else setNewRole('STUDENT');
    }
  }, [isOpen, user]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user) return;
    
    if (newRole === user.role) {
      setError('Please select a different role');
      return;
    }

    onConfirm(newRole, reason.trim() || undefined);
  };

  const handleClose = () => {
    setReason('');
    setError('');
    onClose();
  };

  if (!isOpen || !user) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-lg max-w-md w-full">
        <div className="p-6">
          {/* Header */}
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-text-primary">
              Change User Role
            </h3>
            <button
              onClick={handleClose}
              className="text-text-secondary hover:text-text-primary transition-colors"
              disabled={isLoading}
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* User Info */}
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-sm text-text-secondary mb-1">User:</p>
            <p className="font-medium text-text-primary">{user.fullName}</p>
            <p className="text-sm text-text-secondary">{user.email}</p>
            <div className="mt-2">
              <span className="text-sm text-text-secondary">Current Role: </span>
              <span className="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                {user.role}
              </span>
            </div>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-sm font-medium text-text-primary mb-2">
                New Role *
              </label>
              <select
                value={newRole}
                onChange={(e) => {
                  setNewRole(e.target.value as UserRole);
                  if (error) setError('');
                }}
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors ${
                  error ? 'border-red-500' : 'border-gray-300'
                }`}
                disabled={isLoading}
              >
                <option value="STUDENT">Student</option>
                <option value="MODERATOR">Moderator</option>
                <option value="ADMIN">Admin</option>
              </select>
              {error && (
                <p className="mt-1 text-sm text-red-600">{error}</p>
              )}
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-text-primary mb-2">
                Reason (Optional)
              </label>
              <textarea
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Optional: Provide a reason for this role change..."
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors resize-none"
                rows={3}
                disabled={isLoading}
              />
            </div>

            {/* Role Descriptions */}
            <div className="mb-6 p-3 bg-blue-50 border border-blue-200 rounded-lg">
              <p className="text-sm font-medium text-blue-800 mb-2">Role Permissions:</p>
              <ul className="text-sm text-blue-700 space-y-1">
                <li><strong>Student:</strong> Upload and view resources</li>
                <li><strong>Moderator:</strong> Student + approve/reject resources</li>
                <li><strong>Admin:</strong> Moderator + user management</li>
              </ul>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-end gap-3">
              <Button
                type="button"
                variant="secondary"
                onClick={handleClose}
                disabled={isLoading}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                disabled={isLoading}
              >
                {isLoading ? 'Changing...' : 'Change Role'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
