import { STORAGE_KEYS } from './constants';

export const storage = {
  getToken: (): string | null => {
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  },

  setToken: (token: string): void => {
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
  },

  removeToken: (): void => {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
  },

  getUserData: (): any | null => {
    const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return userData ? JSON.parse(userData) : null;
  },

  setUserData: (userData: any): void => {
    localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
  },

  removeUserData: (): void => {
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  },

  clearAll: (): void => {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  },
};