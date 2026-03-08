import React, { useState } from 'react';
import { Button, Input } from '@/components/ui';

interface RejectModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (reason: string) => void;
  resourceTitle: string;
  isLoading?: boolean;
}

export const RejectModal: React.FC<RejectModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  resourceTitle,
  isLoading = false,
}) => {
  const [reason, setReason] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!reason.trim()) {
      setError('Rejection reason is required');
      return;
    }

    onConfirm(reason.trim());
  };

  const handleClose = () => {
    setReason('');
    setError('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-lg max-w-md w-full">
        <div className="p-6">
          {/* Header */}
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-text-primary">
              Reject Resource
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

          {/* Resource Info */}
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-sm text-text-secondary mb-1">Resource:</p>
            <p className="font-medium text-text-primary">{resourceTitle}</p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit}>
            <div className="mb-6">
              <label className="block text-sm font-medium text-text-primary mb-2">
                Rejection Reason *
              </label>
              <textarea
                value={reason}
                onChange={(e) => {
                  setReason(e.target.value);
                  if (error) setError('');
                }}
                placeholder="Please provide a clear reason for rejecting this resource..."
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors resize-none ${
                  error ? 'border-red-500' : 'border-gray-300'
                }`}
                rows={4}
                disabled={isLoading}
              />
              {error && (
                <p className="mt-1 text-sm text-red-600">{error}</p>
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
                className="bg-red-600 hover:bg-red-700 focus:ring-red-500"
              >
                {isLoading ? 'Rejecting...' : 'Reject Resource'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};