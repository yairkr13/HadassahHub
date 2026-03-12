import React, { useState } from 'react';
import { 
  ModerationStatsComponent, 
  PendingResourcesList 
} from '@/components/admin';
import { ErrorBoundary, ErrorState } from '@/components/common';
import { 
  usePendingResources, 
  useModerationStats, 
  useApproveResource, 
  useRejectResource, 
  useResetResource 
} from '@/hooks/useModeration';

export const ModerationPage: React.FC = () => {
  const [successMessage, setSuccessMessage] = useState<string>('');
  const [errorMessage, setErrorMessage] = useState<string>('');

  // Queries
  const { 
    data: pendingResources = [], 
    isLoading: resourcesLoading, 
    error: resourcesError,
    refetch: refetchResources
  } = usePendingResources();
  
  const { 
    data: stats, 
    isLoading: statsLoading
  } = useModerationStats();

  // Mutations
  const approveMutation = useApproveResource();
  const rejectMutation = useRejectResource();
  const resetMutation = useResetResource();

  const showMessage = (message: string, isError = false) => {
    if (isError) {
      setErrorMessage(message);
      setSuccessMessage('');
    } else {
      setSuccessMessage(message);
      setErrorMessage('');
    }
    
    // Clear message after 5 seconds
    setTimeout(() => {
      setSuccessMessage('');
      setErrorMessage('');
    }, 5000);
  };

  const handleApprove = async (id: number) => {
    try {
      await approveMutation.mutateAsync(id);
      showMessage('Resource approved successfully!');
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to approve resource', true);
    }
  };

  const handleReject = async (id: number, reason: string) => {
    try {
      await rejectMutation.mutateAsync({ id, data: { reason } });
      showMessage('Resource rejected successfully!');
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to reject resource', true);
    }
  };

  const handleReset = async (id: number) => {
    try {
      await resetMutation.mutateAsync(id);
      showMessage('Resource status reset successfully!');
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to reset resource status', true);
    }
  };

  const isAnyMutationLoading = approveMutation.isPending || rejectMutation.isPending || resetMutation.isPending;

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Admin Moderation
          </h1>
          <p className="text-text-secondary">
            Review and moderate pending resource submissions
          </p>
        </div>

        {/* Success/Error Messages */}
        {successMessage && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <div className="flex items-center">
              <svg className="w-5 h-5 text-green-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              <p className="text-green-800">{successMessage}</p>
            </div>
          </div>
        )}

        {errorMessage && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
            <div className="flex items-center">
              <svg className="w-5 h-5 text-red-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
              <p className="text-red-800">{errorMessage}</p>
            </div>
          </div>
        )}

        {/* Moderation Statistics */}
        <ErrorBoundary
          fallback={
            <div className="mb-8 p-6 bg-red-50 border border-red-200 rounded-lg text-center">
              <p className="text-red-800">Failed to load moderation statistics</p>
            </div>
          }
        >
          {stats && (
            <ModerationStatsComponent 
              stats={stats} 
              isLoading={statsLoading} 
            />
          )}
        </ErrorBoundary>

        {/* Pending Resources Section */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-text-primary">
              Pending Resources ({pendingResources.length})
            </h2>
          </div>

          {/* Resources Error State */}
          {resourcesError && (
            <ErrorState
              title="Failed to Load Pending Resources"
              description={
                resourcesError instanceof Error 
                  ? resourcesError.message 
                  : "There was an error loading the pending resources."
              }
              onRetry={() => refetchResources()}
            />
          )}

          {/* Pending Resources List */}
          {!resourcesError && (
            <ErrorBoundary
              fallback={
                <div className="text-center py-12">
                  <div className="w-24 h-24 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <svg className="w-12 h-12 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                    </svg>
                  </div>
                  <h3 className="text-lg font-medium text-text-primary mb-2">
                    Error Loading Resources
                  </h3>
                  <p className="text-text-secondary">
                    There was an error displaying the pending resources. Please refresh the page.
                  </p>
                </div>
              }
            >
              <PendingResourcesList
                resources={pendingResources}
                isLoading={resourcesLoading}
                onApprove={handleApprove}
                onReject={handleReject}
                onReset={handleReset}
                moderationLoading={isAnyMutationLoading}
              />
            </ErrorBoundary>
          )}
        </div>
      </div>
    </div>
  );
};