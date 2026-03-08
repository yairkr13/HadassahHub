export const colors = {
  primary: '#F97316',
  primaryLight: '#FDBA74',
  background: '#FFFFFF',
  backgroundSoft: '#FFF7ED',
  textPrimary: '#1F2937',
  textSecondary: '#6B7280',
} as const;

export const spacing = {
  xs: '8px',
  sm: '16px',
  md: '24px',
  lg: '32px',
  xl: '48px',
} as const;

export const typography = {
  fontFamily: 'Inter, sans-serif',
  fontWeights: {
    regular: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
  },
} as const;