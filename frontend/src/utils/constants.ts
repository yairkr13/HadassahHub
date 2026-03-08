export const API_BASE_URL = 'http://localhost:8080/api';

export const STORAGE_KEYS = {
  AUTH_TOKEN: 'hadassah_hub_token',
  USER_DATA: 'hadassah_hub_user',
} as const;

export const ALLOWED_EMAIL_DOMAINS = [
  '@edu.jmc.ac.il',
  '@edu.hac.ac.il',
] as const;