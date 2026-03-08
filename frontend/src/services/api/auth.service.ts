import { apiClient } from './client';
import { LoginRequest, RegisterRequest, AuthResponse, User } from '@/types/auth.types';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/register', userData);
    return response.data;
  },

  async getProfile(): Promise<User> {
    const response = await apiClient.get<User>('/users/me');
    return response.data;
  },

  async logout(): Promise<void> {
    // For JWT, logout is handled client-side by removing the token
    // If backend has a logout endpoint, it would be called here
    return Promise.resolve();
  },
};