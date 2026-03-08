import React from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from '@/context/AuthContext';

interface AppProvidersProps {
  children: React.ReactNode;
}

// Create a client with optimized cache settings
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes - data is fresh for 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes - keep in cache for 10 minutes
      refetchOnMount: 'always', // Always refetch on mount for fresh data
      refetchOnReconnect: 'always', // Refetch when reconnecting
    },
    mutations: {
      retry: 1,
    },
  },
});

export const AppProviders: React.FC<AppProvidersProps> = ({ children }) => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        {children}
      </AuthProvider>
    </QueryClientProvider>
  );
};