import React from 'react';
import { Logo } from '@/components/common';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-background-soft border-t border-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex flex-col md:flex-row justify-between items-center">
          <div className="mb-4 md:mb-0">
            <Logo />
            <p className="mt-2 text-text-secondary text-sm">
              Academic resource sharing platform for computer science students
            </p>
          </div>
          
          <div className="text-center md:text-right">
            <p className="text-text-secondary text-sm">
              © 2024 HadassahHub. All rights reserved.
            </p>
            <p className="text-text-secondary text-xs mt-1">
              Built for Hadassah Academic College students
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};