import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
  children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled,
  children,
  className = '',
  ...props
}) => {
  const isDisabled = disabled || loading;
  
  const baseClasses = 'font-medium rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 inline-flex items-center justify-center';
  
  const variantClasses = {
    primary: `bg-primary text-white shadow-sm ${
      isDisabled 
        ? 'opacity-60 cursor-not-allowed' 
        : 'hover:bg-primary/90 hover:shadow-md active:bg-primary/95 active:shadow-sm active:scale-[0.98]'
    }`,
    secondary: `bg-white text-primary border border-primary shadow-sm ${
      isDisabled 
        ? 'opacity-60 cursor-not-allowed' 
        : 'hover:bg-primary/5 hover:shadow-md active:bg-primary/10 active:shadow-sm active:scale-[0.98]'
    }`,
  };
  
  const sizeClasses = {
    sm: 'px-4 py-2 text-sm min-h-[2rem]',
    md: 'px-6 py-3 text-base min-h-[2.75rem]',
    lg: 'px-8 py-4 text-lg min-h-[3.5rem]',
  };

  const classes = `${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`;

  return (
    <button 
      className={classes} 
      disabled={isDisabled}
      {...props}
    >
      {loading && (
        <svg 
          className="animate-spin -ml-1 mr-2 h-4 w-4" 
          fill="none" 
          viewBox="0 0 24 24"
        >
          <circle 
            className="opacity-25" 
            cx="12" 
            cy="12" 
            r="10" 
            stroke="currentColor" 
            strokeWidth="4"
          />
          <path 
            className="opacity-75" 
            fill="currentColor" 
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
      )}
      {children}
    </button>
  );
};