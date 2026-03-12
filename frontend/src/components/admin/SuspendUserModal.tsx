import React, { useState } from 'react';
import { Button } from '@/components/ui';
import { AdminUser } from '@/types/admin-user.types';
import { formatDateForInput } from '@/utils/dateUtils';

interface SuspendUserModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (reason: string, expiresAt: string) => void;
  user: AdminUser | null;
  isLoading?: boolean;
}

export const SuspendUserModal: React.FC<SuspendUserModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  user,
  isLoading = false,
}) => {
  const [reason, setReason] = useState('');
  const [expiresAt, setExpiresAt] = useState('');
  const [errors, setErrors] = useState<{ reason?: string; expiresAt?: string }>({});

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    const newErrors: { reason?: string; expiresAt?: string } = {};
    
    if (!reason.trim()) {
      newErrors.reason = 'Suspension reason is required';
    }
    
    if (!expiresAt) {
      newErrors.expiresAt = 'Expiration date is required';
    } else {
      const selectedDate = new Date(expiresAt);
      const now = new Date();
      if (selectedDate <= now) {
        newErrors.expiresAt = 'Expiration date must be in the future';
      }
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    onConfirm(reason.trim(), expiresAt);
  };

  const handleClose = () => {
    setReason('');
    setExpiresAt('');
    setErrors({});
    onClose();
  };

  // Set default expiration to 7 days from now
  React.useEffect(() => {
    if (isOpen && !expiresAt) {
      const defaultDate = new Date();
      defaultDate.setDate(defaultDate.getDate() + 7);
      setExpiresAt(formatDateForInput(defaultDate));
    }
  }, [isOpen]);

  if (!isOpen || !user) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-lg max-w-md w-full">
        <div className="p-6">
          {/* Header */}
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-text-primary">
              Suspend User
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

          {/* Info */}
          <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
            <div className="flex items-start">
              <svg className="w-5 h-5 text-yellow-600 mr-2 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <p className="text-sm font-medium text-yellow-800">Temporary Suspension</p>
                <p className="text-sm text-yellow-700 mt-1">
                  User will be automatically reactivated after the expiration date.
                </p>
              </div>
            </div>
          </div>

          {/* User Info */}
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-sm text-text-secondary mb-1">User:</p>
            <p className="font-medium text-text-primary">{user.fullName}</p>
            <p className="text-sm text-text-secondary">{user.email}</p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-sm font-medium text-text-primary mb-2">
                Suspension Reason *
              </label>
              <textarea
                value={reason}
                onChange={(e) => {
                  setReason(e.target.value);
                  if (errors.reason) setErrors({ ...errors, reason: undefined });
                }}
                placeholder="Please provide a clear reason for suspending this user..."
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors resize-none ${
                  errors.reason ? 'border-red-500' : 'border-gray-300'
                }`}
                rows={3}
                disabled={isLoading}
              />
              {errors.reason && (
                <p className="mt-1 text-sm text-red-600">{errors.reason}</p>
              )}
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-text-primary mb-2">
                Expires At *
              </label>
              <input
                type="datetime-local"
                value={expiresAt}
                onChange={(e) => {
                  setExpiresAt(e.target.value);
                  if (errors.expiresAt) setErrors({ ...errors, expiresAt: undefined });
                }}
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors ${
                  errors.expiresAt ? 'border-red-500' : 'border-gray-300'
                }`}
                disabled={isLoading}
              />
              {errors.expiresAt && (
                <p className="mt-1 text-sm text-red-600">{errors.expiresAt}</p>
              )}
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
                className="bg-yellow-600 hover:bg-yellow-700 focus:ring-yellow-500"
              >
                {isLoading ? 'Suspending...' : 'Suspend User'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
