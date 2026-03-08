import React from 'react';
import { ResourceFilters, ResourceType, ResourceStatus, getResourceTypeDisplayName } from '@/types/resource.types';

interface ResourceFiltersProps {
  filters: ResourceFilters;
  onFiltersChange: (filters: ResourceFilters) => void;
  showStatusFilter?: boolean;
}

export const ResourceFiltersComponent: React.FC<ResourceFiltersProps> = ({
  filters,
  onFiltersChange,
  showStatusFilter = false,
}) => {
  const handleTypeChange = (type: ResourceType | undefined) => {
    onFiltersChange({ ...filters, type });
  };

  const handleStatusChange = (status: ResourceStatus | undefined) => {
    onFiltersChange({ ...filters, status });
  };

  const handleAcademicYearChange = (academicYear: string | undefined) => {
    onFiltersChange({ ...filters, academicYear });
  };

  const handleSearchChange = (search: string) => {
    onFiltersChange({ ...filters, search: search || undefined });
  };

  // Common academic years
  const academicYears = [
    '2024-2025',
    '2023-2024',
    '2022-2023',
    '2021-2022',
  ];

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6 mb-6">
      {/* Search Input */}
      <div className="mb-6">
        <input
          type="text"
          placeholder="Search resources..."
          value={filters.search || ''}
          onChange={(e) => handleSearchChange(e.target.value)}
          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors"
        />
      </div>

      {/* Type Filter */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-text-primary mb-3">
          Resource Type:
        </label>
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => handleTypeChange(undefined)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              !filters.type
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            All Types
          </button>
          {Object.values(ResourceType).map((type) => (
            <button
              key={type}
              onClick={() => handleTypeChange(type)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                filters.type === type
                  ? 'bg-primary text-white'
                  : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
              }`}
            >
              {getResourceTypeDisplayName(type)}
            </button>
          ))}
        </div>
      </div>

      {/* Academic Year Filter */}
      <div className={showStatusFilter ? 'mb-6' : ''}>
        <label className="block text-sm font-medium text-text-primary mb-3">
          Academic Year:
        </label>
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => handleAcademicYearChange(undefined)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              !filters.academicYear
                ? 'bg-primary text-white'
                : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
            }`}
          >
            All Years
          </button>
          {academicYears.map((year) => (
            <button
              key={year}
              onClick={() => handleAcademicYearChange(year)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                filters.academicYear === year
                  ? 'bg-primary text-white'
                  : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
              }`}
            >
              {year}
            </button>
          ))}
        </div>
      </div>

      {/* Status Filter (for My Resources page) */}
      {showStatusFilter && (
        <div>
          <label className="block text-sm font-medium text-text-primary mb-3">
            Status:
          </label>
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => handleStatusChange(undefined)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                !filters.status
                  ? 'bg-primary text-white'
                  : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
              }`}
            >
              All Status
            </button>
            {Object.values(ResourceStatus).map((status) => (
              <button
                key={status}
                onClick={() => handleStatusChange(status)}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                  filters.status === status
                    ? 'bg-primary text-white'
                    : 'bg-gray-100 text-text-secondary hover:bg-gray-200'
                }`}
              >
                {status.charAt(0) + status.slice(1).toLowerCase()}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};