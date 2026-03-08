import React, { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useCourse } from '@/hooks/useCourses';
import { useCourseResources, useCourseResourceStats } from '@/hooks/useResources';
import { Card, Button } from '@/components/ui';
import { ResourceList } from '@/components/resources';
import { ErrorBoundary, SkeletonLoader, EmptyState, ErrorState } from '@/components/common';
import { mapBackendCategoryToDisplay, CourseCategoryDisplay } from '@/types/course.types';
import { ResourceFilters } from '@/types/resource.types';

const getCategoryDisplayName = (category: CourseCategoryDisplay): string => {
  switch (category) {
    case CourseCategoryDisplay.CS_MANDATORY:
      return 'CS Mandatory';
    case CourseCategoryDisplay.CS_ELECTIVE:
      return 'CS Elective';
    case CourseCategoryDisplay.COLLEGE_GENERAL:
      return 'College Course';
    default:
      return 'Course';
  }
};

export const CourseDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const courseId = id ? parseInt(id, 10) : 0;
  const [resourceFilters, setResourceFilters] = useState<ResourceFilters>({});
  
  const { data: course, isLoading: courseLoading, error: courseError, refetch: refetchCourse } = useCourse(courseId);
  const { 
    data: resources = [], 
    isLoading: resourcesLoading, 
    error: resourcesError,
    refetch: refetchResources
  } = useCourseResources(courseId, resourceFilters);
  const { data: resourceStats } = useCourseResourceStats(courseId);

  if (courseLoading) {
    return (
      <div className="min-h-screen bg-background-soft">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Breadcrumb skeleton */}
          <div className="mb-6">
            <SkeletonLoader width="w-32" height="h-4" />
          </div>

          {/* Course header skeleton */}
          <Card className="mb-8">
            <div className="p-8">
              <div className="animate-pulse">
                <SkeletonLoader width="w-24" height="h-6" className="mb-3" />
                <SkeletonLoader width="w-3/4" height="h-8" className="mb-4" />
                <div className="space-y-2 mb-4">
                  <SkeletonLoader width="w-full" height="h-5" />
                  <SkeletonLoader width="w-5/6" height="h-5" />
                </div>
                <SkeletonLoader width="w-20" height="h-4" />
              </div>
            </div>
          </Card>

          {/* Stats skeleton */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            {Array.from({ length: 4 }).map((_, index) => (
              <div key={index} className="bg-white rounded-lg shadow-sm border p-6">
                <div className="animate-pulse flex items-center">
                  <SkeletonLoader width="w-8" height="h-8" className="rounded-lg mr-4" />
                  <div className="flex-1">
                    <SkeletonLoader width="w-20" height="h-4" className="mb-2" />
                    <SkeletonLoader width="w-8" height="h-6" />
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Resources section skeleton */}
          <div className="mb-6">
            <div className="flex items-center justify-between mb-6">
              <SkeletonLoader width="w-40" height="h-7" />
              <SkeletonLoader width="w-32" height="h-10" />
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {Array.from({ length: 3 }).map((_, index) => (
                <div key={index} className="bg-white rounded-lg shadow-sm border p-6">
                  <div className="animate-pulse">
                    <div className="flex items-center justify-between mb-3">
                      <SkeletonLoader width="w-16" height="h-5" />
                      <SkeletonLoader width="w-12" height="h-4" />
                    </div>
                    <SkeletonLoader width="w-3/4" height="h-6" className="mb-2" />
                    <div className="space-y-2 mb-4">
                      <SkeletonLoader width="w-full" height="h-4" />
                      <SkeletonLoader width="w-2/3" height="h-4" />
                    </div>
                    <div className="flex items-center justify-between">
                      <SkeletonLoader width="w-16" height="h-4" />
                      <SkeletonLoader width="w-20" height="h-8" />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (courseError || !course) {
    return (
      <div className="min-h-screen bg-background-soft">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <ErrorState
            title="Course Not Found"
            description={
              courseError instanceof Error 
                ? courseError.message 
                : "The course you're looking for doesn't exist or has been removed."
            }
            onRetry={() => refetchCourse()}
          />
          <div className="text-center mt-6">
            <Link to="/courses">
              <Button variant="secondary">Back to Courses</Button>
            </Link>
          </div>
        </div>
      </div>
    );
  }

  const displayCategory = mapBackendCategoryToDisplay(course.category);
  const categoryName = getCategoryDisplayName(displayCategory);

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Breadcrumb */}
        <nav className="mb-6">
          <Link 
            to="/courses" 
            className="text-primary hover:text-primary-dark transition-colors"
          >
            ← Back to Courses
          </Link>
        </nav>

        {/* Course Header */}
        <Card className="mb-8 p-8">
          <div className="flex items-start justify-between mb-6">
            <div>
              <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-primary/10 text-primary mb-4 transition-colors">
                {categoryName}
              </span>
              <h1 className="text-3xl font-bold text-text-primary mb-3 leading-tight">
                {course.name}
              </h1>
            </div>
            {course.recommendedYear && (
              <span className="text-sm text-text-secondary bg-gray-50 px-3 py-2 rounded-full border">
                Recommended: Year {course.recommendedYear}
              </span>
            )}
          </div>
          
          <p className="text-text-secondary text-lg mb-6 leading-relaxed">
            {course.description}
          </p>
          
          <div className="flex items-center gap-6 text-sm text-text-secondary">
            <span className="flex items-center gap-2 bg-gray-50 px-3 py-2 rounded-lg">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {course.credits} {course.credits === 1 ? 'Credit' : 'Credits'}
            </span>
          </div>
        </Card>

        {/* Resource Statistics */}
        {resourceStats && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Total Resources</p>
                  <p className="text-2xl font-bold text-text-primary">{resourceStats.totalResources}</p>
                </div>
              </div>
            </Card>

            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Approved</p>
                  <p className="text-2xl font-bold text-text-primary">{resourceStats.approvedResources}</p>
                </div>
              </div>
            </Card>

            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-red-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Exams</p>
                  <p className="text-2xl font-bold text-text-primary">{resourceStats.resourcesByType.EXAM || 0}</p>
                </div>
              </div>
            </Card>

            <Card hover className="p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-text-secondary mb-1">Summaries</p>
                  <p className="text-2xl font-bold text-text-primary">{resourceStats.resourcesByType.SUMMARY || 0}</p>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* Resources Section */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-text-primary">
              Course Resources
            </h2>
            <Link to={`/courses/${courseId}/upload`}>
              <Button variant="primary">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                Upload Resource
              </Button>
            </Link>
          </div>

          <ErrorBoundary
            fallback={
              <ErrorState
                title="Error Loading Resources"
                description="There was an error displaying the course resources."
                onRetry={() => refetchResources()}
              />
            }
          >
            {resourcesError ? (
              <ErrorState
                title="Failed to Load Resources"
                description={
                  resourcesError instanceof Error 
                    ? resourcesError.message 
                    : "There was an error loading the course resources."
                }
                onRetry={() => refetchResources()}
              />
            ) : (
              <ResourceList
                resources={resources}
                isLoading={resourcesLoading}
                showCourse={false}
                showActions={false}
                showFilters={true}
                filters={resourceFilters}
                onFiltersChange={setResourceFilters}
                emptyMessage="No resources available for this course yet"
              />
            )}
          </ErrorBoundary>
        </div>
      </div>
    </div>
  );
};