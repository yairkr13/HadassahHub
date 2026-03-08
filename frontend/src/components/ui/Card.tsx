import React from 'react';

interface CardProps {
  children: React.ReactNode;
  className?: string;
  hover?: boolean;
  clickable?: boolean;
  onClick?: () => void;
}

export const Card: React.FC<CardProps> = ({ 
  children, 
  className = '', 
  hover = false,
  clickable = false,
  onClick
}) => {
  const baseClasses = 'bg-white rounded-lg shadow-sm border border-gray-100 transition-all duration-200';
  
  const interactiveClasses = hover || clickable || onClick 
    ? 'hover:shadow-md hover:-translate-y-0.5 hover:border-gray-200' 
    : '';
    
  const clickableClasses = clickable || onClick 
    ? 'cursor-pointer active:scale-[0.99] active:shadow-sm' 
    : '';

  const classes = `${baseClasses} ${interactiveClasses} ${clickableClasses} ${className}`;

  return (
    <div className={classes} onClick={onClick}>
      {children}
    </div>
  );
};