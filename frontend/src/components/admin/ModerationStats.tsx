import React from 'react';
import { ModerationStats } from '@/types/moderation.types';

interface ModerationStatsProps {
  stats: ModerationStats;
  isLoading?: boolean;
}

export const ModerationStatsComponent: React.FC<ModerationStatsProps> = ({
  stats,
  isLoading = false,
}) => {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-8">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="bg-white rounded-lg shadow-sm border p-6 animate-pulse">
            <div className="h-4 bg-gray-200 rounded mb-2"></div>
            <div className="h-8 bg-gray-200 rounded"></div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-8">
      {/* Pending Resources */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <div className="w-8 h-8 bg-yellow-100 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-text-secondary">Pending</p>
            <p className="text-2xl font-bold text-text-primary">{stats.totalPending}</p>
          </div>
        </div>
      </div>

      {/* Total Approved */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <div className="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-text-secondary">Total Approved</p>
            <p className="text-2xl font-bold text-text-primary">{stats.totalApproved}</p>
          </div>
        </div>
      </div>

      {/* Total Rejected */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <div className="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-text-secondary">Total Rejected</p>
            <p className="text-2xl font-bold text-text-primary">{stats.totalRejected}</p>
          </div>
        </div>
      </div>

      {/* Approved Today */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-text-secondary">Approved Today</p>
            <p className="text-2xl font-bold text-text-primary">{stats.approvedToday}</p>
          </div>
        </div>
      </div>

      {/* Rejected Today */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <div className="w-8 h-8 bg-orange-100 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
            </div>
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-text-secondary">Rejected Today</p>
            <p className="text-2xl font-bold text-text-primary">{stats.rejectedToday}</p>
          </div>
        </div>
      </div>
    </div>
  );
};