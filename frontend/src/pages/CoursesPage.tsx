import React, { useState } from 'react';
import { CourseCard, CourseFiltersComponent } from '@/components/courses';
import { SkeletonGrid, EmptyState, ErrorState } from '@/components/common';
import { useCourses } from '@/hooks/useCourses';
import { CourseFilters } from '@/types/course.types';

export const CoursesPage: React.FC = () => {
  const [filters, setFilters] = useState<CourseFilters>({});
  const { data: courses, isLoading, error, refetch } = useCourses(filters);

  const handleFiltersChange = (newFilters: CourseFilters) => {
    setFilters(newFilters);
  };

  const hasActiveFilters = Object.keys(filters).some(key => 
    filters[key as keyof CourseFilters] !== undefined && 
    filters[key as keyof CourseFilters] !== ''
  );

  if (error) {
    return (
      <div className="min-h-screen bg-background-soft">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-text-primary mb-2">
              Course Catalog
            </h1>
            <p className="text-text-secondary">
              Browse and discover computer science courses and their resources
            </p>
          </div>
          
          <ErrorState
            title="Error Loading Courses"
            description={error instanceof Error ? error.message : 'Failed to load courses. Please try again.'}
            onRetry={() => refetch()}
          />
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Course Catalog
          </h1>
          <p className="text-text-secondary">
            Browse and discover computer science courses and their resources
          </p>
        </div>

        {/* Filters */}
        <CourseFiltersComponent 
          filters={filters} 
          onFiltersChange={handleFiltersChange} 
        />

        {/* Loading State */}
        {isLoading && (
          <>
            <div className="mb-6">
              <div className="animate-pulse bg-gray-200 rounded h-4 w-32"></div>
            </div>
            <SkeletonGrid count={6} />
          </>
        )}

        {/* Course Grid */}
        {!isLoading && courses && (
          <>
            {/* Results Count */}
            <div className="mb-6">
              <p className="text-text-secondary">
                {courses.length} {courses.length === 1 ? 'course' : 'courses'} found
              </p>
            </div>

            {/* Course Cards */}
            {courses.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {courses.map((course) => (
                  <CourseCard key={course.id} course={course} />
                ))}
              </div>
            ) : (
              <EmptyState
                icon={
                  <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  </svg>
                }
                title={hasActiveFilters ? "No courses match your filters" : "No courses available"}
                description={
                  hasActiveFilters 
                    ? "Try adjusting your search criteria or clearing some filters to see more results."
                    : "There are no courses available at the moment. Please check back later."
                }
                action={hasActiveFilters ? {
                  label: "Clear Filters",
                  onClick: () => setFilters({}),
                  variant: "secondary"
                } : undefined}
              />
            )}
          </>
        )}
      </div>
    </div>
  );
};