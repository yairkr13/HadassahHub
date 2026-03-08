import React from 'react';
import { Link } from 'react-router-dom';
import { Button, Card } from '@/components/ui';
import { ROUTES } from '@/router/routes';

export const CoursesPlaceholderPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-background-soft flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl w-full text-center">
        <Card>
          <div className="py-12">
            <div className="w-24 h-24 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg className="w-12 h-12 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            
            <h1 className="text-3xl font-bold text-text-primary mb-4">
              Course Catalog Coming Soon!
            </h1>
            
            <p className="text-lg text-text-secondary mb-8">
              We're working hard to bring you the complete course catalog with all your 
              computer science courses and resources. This feature will be available soon.
            </p>
            
            <div className="space-y-4">
              <p className="text-text-secondary">
                In the meantime, you can:
              </p>
              
              <div className="flex flex-col sm:flex-row gap-4 justify-center">
                <Link to={ROUTES.HOME}>
                  <Button variant="primary">
                    Back to Homepage
                  </Button>
                </Link>
                <Link to={ROUTES.LOGIN}>
                  <Button variant="secondary">
                    Sign In to Your Account
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};