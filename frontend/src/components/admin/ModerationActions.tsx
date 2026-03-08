import React, { useState } from 'react';
import { Button } from '@/components/ui';
import { Resource } from '@/types/resource.types';
import { RejectModal } from './RejectModal';

interface ModerationActionsProps {
  resource: Resource;
  onApprove: (id: number) => void;
  onReject: (id: number, reason: string) => void;
  onReset: (id: number) => void;
  isLoading?: boolean;
}

export const ModerationActions: React.FC<ModerationActionsProps> = ({
  resource,
  onApprove,
  onReject,
  onReset,
  isLoading = false,
}) => {
  const [showRejectModal, setShowRejectModal] = useState(false);

  const handleApprove = () => {
    if (window.confirm(`Are you sure you want to approve "${resource.title}"?`)) {
      onApprove(resource.id);
    }
  };

  const handleReject = (reason: string) => {
    onReject(resource.id, reason);
    setShowRejectModal(false);
  };

  const handleReset = () => {
    if (window.confirm(`Are you sure you want to reset the moderation status for "${resource.title}"?`)) {
      onReset(resource.id);
    }
  };

  return (
    <>
      <div className="flex items-center gap-2">
        {/* Approve Button */}
        <Button
          variant="primary"
          size="sm"
          onClick={handleApprove}
          disabled={isLoading}
          className="bg-green-600 hover:bg-green-700 focus:ring-green-500"
        >
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
          </svg>
          Approve
        </Button>

        {/* Reject Button */}
        <Button
          variant="secondary"
          size="sm"
          onClick={() => setShowRejectModal(true)}
          disabled={isLoading}
          className="border-red-300 text-red-600 hover:bg-red-50 focus:ring-red-500"
        >
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
          Reject
        </Button>

        {/* Reset Button */}
        <Button
          variant="secondary"
          size="sm"
          onClick={handleReset}
          disabled={isLoading}
          className="border-gray-300 text-gray-600 hover:bg-gray-50"
        >
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Reset
        </Button>
      </div>

      {/* Reject Modal */}
      <RejectModal
        isOpen={showRejectModal}
        onClose={() => setShowRejectModal(false)}
        onConfirm={handleReject}
        resourceTitle={resource.title}
        isLoading={isLoading}
      />
    </>
  );
};