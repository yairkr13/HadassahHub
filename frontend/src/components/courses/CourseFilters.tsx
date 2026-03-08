import React from 'react';
import { CourseCategoryDisplay, CourseFilters } from '@/types/course.types';

interface CourseFiltersProps {
  filters: CourseFilters;
  onFiltersChange: (filters: CourseFilters) => void;
}

export const CourseFiltersComponent: React.FC<CourseFiltersProps> = ({
  filters,
  onFiltersChange,
}) => {
  const handleCategoryChange = (category: CourseCategoryDisplay | undefined) => {
    onFiltersChange({ ...filters, category });
  };

  const handleYearChange = (year: number | undefined) => {
    onFiltersChange({ ...filters, year });
  };

  const handleSearchChange = (search: string) => {
    onFiltersChange({ ...filters, search: search || undefined });
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6 mb-6">
      {/* Search Input */}
      <div className="mb-6">
        <input
          type="text"
          placeholder="Search courses..."
          value={filters.search || ''}
          onChange={(e) => handleSearchChange(e.target.value)}
          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors"
        />
      </div>

      {/* Category Filter */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-text-primary mb-3">
          Category:
        </label>
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => handleCategoryChange(undefined)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              !filters.category
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            All
          </button>
          <button
            onClick={() => handleCategoryChange(CourseCategoryDisplay.CS_MANDATORY)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              filters.category === CourseCategoryDisplay.CS_MANDATORY
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            CS Mandatory
          </button>
          <button
            onClick={() => handleCategoryChange(CourseCategoryDisplay.CS_ELECTIVE)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              filters.category === CourseCategoryDisplay.CS_ELECTIVE
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            CS Electives
          </button>
          <button
            onClick={() => handleCategoryChange(CourseCategoryDisplay.COLLEGE_GENERAL)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              filters.category === CourseCategoryDisplay.COLLEGE_GENERAL
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            College Courses
          </button>
        </div>
      </div>

      {/* Year Filter */}
      <div>
        <label className="block text-sm font-medium text-text-primary mb-3">
          Year:
        </label>
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => handleYearChange(undefined)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              !filters.year
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            All
          </button>
          {[1, 2, 3, 4].map((year) => (
            <button
              key={year}
              onClick={() => handleYearChange(year)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                filters.year === year
                  ? 'bg-primary text-white'
                  : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
              }`}
            >
              Year {year}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};