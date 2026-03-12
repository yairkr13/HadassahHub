import React from 'react';
import { UserRole, UserStatus, UserFilters as UserFiltersType } from '@/types/admin-user.types';
import { Input } from '@/components/ui';

interface UserFiltersProps {
  filters: UserFiltersType;
  onFilterChange: (filters: UserFiltersType) => void;
}

export const UserFilters: React.FC<UserFiltersProps> = ({ filters, onFilterChange }) => {
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onFilterChange({ ...filters, search: e.target.value, page: 0 });
  };

  const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const role = e.target.value === '' ? undefined : (e.target.value as UserRole);
    onFilterChange({ ...filters, role, page: 0 });
  };

  const handleStatusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const status = e.target.value === '' ? undefined : (e.target.value as UserStatus);
    onFilterChange({ ...filters, status, page: 0 });
  };

  const handleClearFilters = () => {
    onFilterChange({ page: 0, size: filters.size });
  };

  const hasActiveFilters = filters.search || filters.role || filters.status;

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Search Input */}
        <div>
          <Input
            type="text"
            placeholder="Search by name or email..."
            value={filters.search || ''}
            onChange={handleSearchChange}
            className="w-full"
          />
        </div>

        {/* Role Filter */}
        <div>
          <select
            value={filters.role || ''}
            onChange={handleRoleChange}
            className="input-field w-full"
          >
            <option value="">All Roles</option>
            <option value="STUDENT">Student</option>
            <option value="MODERATOR">Moderator</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>

        {/* Status Filter */}
        <div>
          <select
            value={filters.status || ''}
            onChange={handleStatusChange}
            className="input-field w-full"
          >
            <option value="">All Statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="BLOCKED">Blocked</option>
            <option value="SUSPENDED">Suspended</option>
          </select>
        </div>
      </div>

      {/* Clear Filters Button */}
      {hasActiveFilters && (
        <div className="mt-4 flex justify-end">
          <button
            onClick={handleClearFilters}
            className="text-sm text-primary hover:text-primary/80 font-medium"
          >
            Clear Filters
          </button>
        </div>
      )}
    </div>
  );
};
