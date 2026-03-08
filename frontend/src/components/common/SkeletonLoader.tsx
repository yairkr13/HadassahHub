import React from 'react';

interface SkeletonLoaderProps {
  className?: string;
  width?: string;
  height?: string;
}

export const SkeletonLoader: React.FC<SkeletonLoaderProps> = ({
  className = '',
  width = 'w-full',
  height = 'h-4',
}) => {
  return (
    <div
      className={`animate-pulse bg-gray-200 rounded ${width} ${height} ${className}`}
    />
  );
};

interface SkeletonCardProps {
  className?: string;
}

export const SkeletonCard: React.FC<SkeletonCardProps> = ({ className = '' }) => {
  return (
    <div className={`bg-white rounded-lg shadow-sm border p-6 ${className}`}>
      <div className="animate-pulse">
        {/* Header with badge */}
        <div className="flex items-center justify-between mb-3">
          <SkeletonLoader width="w-20" height="h-5" />
          <SkeletonLoader width="w-12" height="h-4" />
        </div>
        
        {/* Title */}
        <SkeletonLoader width="w-3/4" height="h-6" className="mb-2" />
        
        {/* Description lines */}
        <div className="space-y-2 mb-4">
          <SkeletonLoader width="w-full" height="h-4" />
          <SkeletonLoader width="w-5/6" height="h-4" />
          <SkeletonLoader width="w-2/3" height="h-4" />
        </div>
        
        {/* Footer */}
        <div className="flex items-center justify-between">
          <SkeletonLoader width="w-16" height="h-4" />
          <SkeletonLoader width="w-5" height="h-5" />
        </div>
      </div>
    </div>
  );
};

interface SkeletonGridProps {
  count?: number;
  className?: string;
}

export const SkeletonGrid: React.FC<SkeletonGridProps> = ({ 
  count = 6, 
  className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6' 
}) => {
  return (
    <div className={className}>
      {Array.from({ length: count }).map((_, index) => (
        <SkeletonCard key={index} />
      ))}
    </div>
  );
};