import React from 'react';

interface LogoProps {
  className?: string;
}

export const Logo: React.FC<LogoProps> = ({ className = '' }) => {
  return (
    <div className={`flex items-center ${className}`}>
      <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center mr-2">
        <span className="text-white font-bold text-lg">H</span>
      </div>
      <span className="text-xl font-bold text-text-primary">HadassahHub</span>
    </div>
  );
};