import React from 'react';
import { Link } from 'react-router-dom';
import { Button, Card } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/router/routes';

export const HomePage: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="bg-background">
      {/* Hero Section */}
      <section className="bg-background-soft">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold text-text-primary mb-6">
              Share and Discover Academic Resources for Computer Science Courses
            </h1>
            <p className="text-xl text-text-secondary mb-8 max-w-3xl mx-auto">
              Connect with fellow students, share study materials, and access a comprehensive 
              library of exams, homework, summaries, and useful links for your CS courses.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link to={ROUTES.COURSES}>
                <Button variant="primary" size="lg">
                  Browse Courses
                </Button>
              </Link>
              {!isAuthenticated && (
                <Link to={ROUTES.LOGIN}>
                  <Button variant="secondary" size="lg">
                    Sign In
                  </Button>
                </Link>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-text-primary mb-4">
              Everything You Need for Academic Success
            </h2>
            <p className="text-lg text-text-secondary max-w-2xl mx-auto">
              Our platform provides a comprehensive solution for sharing and discovering 
              academic resources in a moderated, student-friendly environment.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <Card>
              <div className="text-center">
                <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                  <svg className="w-8 h-8 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                  </svg>
                </div>
                <h3 className="text-xl font-semibold text-text-primary mb-2">
                  Share Materials
                </h3>
                <p className="text-text-secondary">
                  Upload and share your study materials including exams, homework solutions, 
                  course summaries, and helpful links with your fellow students.
                </p>
              </div>
            </Card>

            {/* Feature 2 */}
            <Card>
              <div className="text-center">
                <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                  <svg className="w-8 h-8 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
                <h3 className="text-xl font-semibold text-text-primary mb-2">
                  Browse Course Resources
                </h3>
                <p className="text-text-secondary">
                  Discover resources organized by course, filter by type and academic year, 
                  and find exactly what you need for your studies.
                </p>
              </div>
            </Card>

            {/* Feature 3 */}
            <Card>
              <div className="text-center">
                <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                  <svg className="w-8 h-8 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <h3 className="text-xl font-semibold text-text-primary mb-2">
                  Community Moderation
                </h3>
                <p className="text-text-secondary">
                  All content is reviewed by our moderation team to ensure quality, 
                  relevance, and academic integrity for a trusted learning environment.
                </p>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section id="about" className="bg-background-soft py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold text-text-primary mb-6">
              Built for Hadassah Academic College Students
            </h2>
            <p className="text-lg text-text-secondary mb-8">
              HadassahHub is designed specifically for computer science students at 
              Hadassah Academic College. Our platform facilitates knowledge sharing 
              and collaboration while maintaining academic standards through careful moderation.
            </p>
            <p className="text-text-secondary">
              Join our community of students helping students succeed in their academic journey.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
};