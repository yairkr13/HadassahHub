import React from 'react';
import { Link } from 'react-router-dom';
import { Course, mapBackendCategoryToDisplay, CourseCategoryDisplay } from '@/types/course.types';
import { Card } from '@/components/ui';

interface CourseCardProps {
  course: Course;
}

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

const getCategoryColor = (category: CourseCategoryDisplay): string => {
  switch (category) {
    case CourseCategoryDisplay.CS_MANDATORY:
      return 'bg-red-100 text-red-800';
    case CourseCategoryDisplay.CS_ELECTIVE:
      return 'bg-blue-100 text-blue-800';
    case CourseCategoryDisplay.COLLEGE_GENERAL:
      return 'bg-green-100 text-green-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

const CourseCardComponent: React.FC<CourseCardProps> = ({ course }) => {
  const displayCategory = mapBackendCategoryToDisplay(course.category);
  const categoryName = getCategoryDisplayName(displayCategory);
  const categoryColor = getCategoryColor(displayCategory);

  return (
    <Link to={`/courses/${course.id}`} className="block group">
      <Card hover className="h-full p-6">
        {/* Category Badge */}
        <div className="flex items-center justify-between mb-3">
          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium transition-colors ${categoryColor}`}>
            {categoryName}
          </span>
          {course.recommendedYear && (
            <span className="text-xs text-text-secondary bg-gray-50 px-2 py-1 rounded-full">
              Year {course.recommendedYear}
            </span>
          )}
        </div>

        {/* Course Title */}
        <h3 className="text-lg font-semibold text-text-primary mb-3 line-clamp-2 group-hover:text-primary transition-colors">
          {course.name}
        </h3>

        {/* Course Description */}
        <p className="text-text-secondary text-sm mb-4 line-clamp-3 leading-relaxed">
          {course.description}
        </p>

        {/* Course Credits and Arrow */}
        <div className="flex items-center justify-between mt-auto">
          <span className="text-sm text-text-secondary font-medium">
            {course.credits} {course.credits === 1 ? 'Credit' : 'Credits'}
          </span>
          
          {/* Arrow Icon with animation */}
          <div className="flex items-center justify-center w-8 h-8 rounded-full bg-primary/10 group-hover:bg-primary/20 transition-all duration-200">
            <svg 
              className="w-4 h-4 text-primary group-hover:translate-x-0.5 transition-transform duration-200" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M9 5l7 7-7 7" 
              />
            </svg>
          </div>
        </div>
      </Card>
    </Link>
  );
};

// Memoize the component to prevent unnecessary re-renders
export const CourseCard = React.memo(CourseCardComponent);