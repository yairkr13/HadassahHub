import React, { useState } from 'react';
import { Button } from '@/components/ui';
import { useApproveResource, useRejectResource } from '@/hooks/useModeration';

interface ModerationActionsProps {
  resourceId: number;
  resourceTitle: string;
  onSuccess?: () => void;
}

export const ModerationActions: React.FC<ModerationActionsProps> = ({
  resourceId,
  resourceTitle,
  onSuccess,
}) => {
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectionReason, setRejectionReason] = useState('');
  
  const approveMutation = useApproveResource();
  const rejectMutation = useRejectResource();

  const handleApprove = async () => {
    try {
      await approveMutation.mutateAsync(resourceId);
      onSuccess?.();
    } catch (error) {
      console.error('Error approving resource:', error);
      alert('Failed to approve resource');
    }
  };

  const handleReject = async () => {
    if (!rejectionReason.trim()) {
      alert('Please provide a rejection reason');
      return;
    }

    try {
      await rejectMutation.mutateAsync({
        id: resourceId,
        data: { reason: rejectionReason },
      });
      setShowRejectModal(false);
      setRejectionReason('');
      onSuccess?.();
    } catch (error) {
      console.error('Error rejecting resource:', error);
      alert('Failed to reject resource');
    }
  };

  const commonReasons = [
    'Content is not appropriate for academic use',
    'The provided URL is not accessible or broken',
    'Resource does not match the selected course',
    'This resource has already been uploaded',
    'Resource quality does not meet standards',
    'Resource may violate copyright restrictions',
    'Resource appears to be spam or irrelevant',
  ];

  return (
    <>
      <div className="flex items-center gap-2">
        <Button
          variant="primary"
          size="sm"
          onClick={handleApprove}
          disabled={approveMutation.isPending}
        >
          {approveMutation.isPending ? 'Approving...' : 'Approve'}
        </Button>
        <Button
          variant="secondary"
          size="sm"
          onClick={() => setShowRejectModal(true)}
          disabled={rejectMutation.isPending}
        >
          Reject
        </Button>
      </div>

      {/* Reject Modal */}
      {showRejectModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">Reject Resource</h3>
            <p className="text-sm text-gray-600 mb-4">
              Resource: <strong>{resourceTitle}</strong>
            </p>

            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">
                Select common reason:
              </label>
              <select
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
              >
                <option value="">-- Select a reason --</option>
                {commonReasons.map((reason) => (
                  <option key={reason} value={reason}>
                    {reason}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">
                Or provide custom reason:
              </label>
              <textarea
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                rows={4}
                maxLength={500}
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                placeholder="Enter rejection reason..."
              />
              <p className="text-xs text-gray-500 mt-1">
                {rejectionReason.length}/500 characters
              </p>
            </div>

            <div className="flex items-center justify-end gap-3">
              <Button
                variant="secondary"
                onClick={() => {
                  setShowRejectModal(false);
                  setRejectionReason('');
                }}
                disabled={rejectMutation.isPending}
              >
                Cancel
              </Button>
              <Button
                variant="primary"
                onClick={handleReject}
                disabled={!rejectionReason.trim() || rejectMutation.isPending}
              >
                {rejectMutation.isPending ? 'Rejecting...' : 'Reject Resource'}
              </Button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
