import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from './routes';

interface AuthRedirectProps {
  children: React.ReactNode;
}

/**
 * Redirects authenticated users away from login/register pages
 */
export const AuthRedirect: React.FC<AuthRedirectProps> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background-soft flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-text-secondary">Loading...</p>
        </div>
      </div>
    );
  }

  if (isAuthenticated) {
    // Redirect authenticated users to courses page
    return <Navigate to={ROUTES.COURSES} replace />;
  }

  return <>{children}</>;
};