import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { User, AuthContextType, LoginRequest, RegisterRequest } from '@/types/auth.types';
import { authService } from '@/services/api/auth.service';
import { storage } from '@/utils/storage';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [authError, setAuthError] = useState<string | null>(null);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        setAuthError(null);
        const storedToken = storage.getToken();
        const storedUser = storage.getUserData();

        if (storedToken && storedUser) {
          // Set initial state from storage for immediate UI update
          setToken(storedToken);
          setUser(storedUser);
          
          // Verify token with backend in background
          try {
            const currentUser = await authService.getProfile();
            // Update user data if it changed
            if (JSON.stringify(currentUser) !== JSON.stringify(storedUser)) {
              setUser(currentUser);
              storage.setUserData(currentUser);
            }
          } catch (error) {
            // Token is invalid or expired, clear auth state
            console.log('Token verification failed, clearing auth state');
            setAuthError('Your session has expired. Please sign in again.');
            storage.clearAll();
            setToken(null);
            setUser(null);
          }
        }
      } catch (error) {
        console.error('Auth initialization error:', error);
        setAuthError('Failed to initialize authentication');
        storage.clearAll();
        setToken(null);
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  const login = async (credentials: LoginRequest): Promise<void> => {
    try {
      setAuthError(null);
      const response = await authService.login(credentials);
      
      setToken(response.token);
      setUser(response.user);
      
      storage.setToken(response.token);
      storage.setUserData(response.user);
    } catch (error) {
      setAuthError('Login failed. Please check your credentials.');
      throw error;
    }
  };

  const register = async (userData: RegisterRequest): Promise<void> => {
    try {
      setAuthError(null);
      const response = await authService.register(userData);
      
      setToken(response.token);
      setUser(response.user);
      
      storage.setToken(response.token);
      storage.setUserData(response.user);
    } catch (error) {
      setAuthError('Registration failed. Please try again.');
      throw error;
    }
  };

  const logout = (): void => {
    setToken(null);
    setUser(null);
    setAuthError(null);
    storage.clearAll();
  };

  const clearAuthError = (): void => {
    setAuthError(null);
  };

  const value: AuthContextType = {
    user,
    token,
    isAuthenticated: !!token && !!user,
    isLoading,
    authError,
    login,
    register,
    logout,
    clearAuthError,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

