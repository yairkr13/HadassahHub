import React from 'react';
import { Button } from '@/components/ui';

interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  description: string;
  action?: {
    label: string;
    onClick: () => void;
    variant?: 'primary' | 'secondary';
  };
  className?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon,
  title,
  description,
  action,
  className = '',
}) => {
  const defaultIcon = (
    <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
    </svg>
  );

  return (
    <div className={`text-center py-12 ${className}`}>
      <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
        {icon || defaultIcon}
      </div>
      <h3 className="text-lg font-medium text-text-primary mb-2">
        {title}
      </h3>
      <p className="text-text-secondary mb-6 max-w-md mx-auto">
        {description}
      </p>
      {action && (
        <Button
          variant={action.variant || 'primary'}
          onClick={action.onClick}
        >
          {action.label}
        </Button>
      )}
    </div>
  );
};

interface ErrorStateProps {
  title?: string;
  description?: string;
  onRetry?: () => void;
  className?: string;
}

export const ErrorState: React.FC<ErrorStateProps> = ({
  title = 'Something went wrong',
  description = 'We encountered an error while loading the data. Please try again.',
  onRetry,
  className = '',
}) => {
  const errorIcon = (
    <svg className="w-12 h-12 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
    </svg>
  );

  return (
    <div className={`text-center py-12 ${className}`}>
      <div className="w-24 h-24 bg-red-50 rounded-full flex items-center justify-center mx-auto mb-6">
        {errorIcon}
      </div>
      <h3 className="text-lg font-medium text-text-primary mb-2">
        {title}
      </h3>
      <p className="text-text-secondary mb-6 max-w-md mx-auto">
        {description}
      </p>
      {onRetry && (
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button variant="primary" onClick={onRetry}>
            Try Again
          </Button>
          <Button 
            variant="secondary" 
            onClick={() => window.location.reload()}
          >
            Refresh Page
          </Button>
        </div>
      )}
    </div>
  );
};