import React from 'react';
import { AdminUser, UserRole, UserStatus } from '@/types/admin-user.types';
import { formatDate } from '@/utils/dateUtils';

interface UserTableProps {
  users: AdminUser[];
  isLoading: boolean;
  onViewDetails: (userId: number) => void;
  onBlockUser: (userId: number) => void;
  onSuspendUser: (userId: number) => void;
  onActivateUser: (userId: number) => void;
  onChangeRole: (userId: number) => void;
}

export const UserTable: React.FC<UserTableProps> = ({
  users,
  isLoading,
  onViewDetails,
  onBlockUser,
  onSuspendUser,
  onActivateUser,
  onChangeRole,
}) => {
  const getRoleBadgeColor = (role: UserRole): string => {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-800';
      case 'MODERATOR':
        return 'bg-blue-100 text-blue-800';
      case 'STUDENT':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusBadgeColor = (status: UserStatus): string => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'BLOCKED':
        return 'bg-red-100 text-red-800';
      case 'SUSPENDED':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
        <div className="flex justify-center items-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
      </div>
    );
  }

  if (users.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 text-center">
        <p className="text-text-secondary">No users found</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                User
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Role
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Registration
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Last Login
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Resources
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="flex flex-col">
                    <div className="text-sm font-medium text-gray-900">{user.fullName}</div>
                    <div className="text-sm text-gray-500">{user.email}</div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${getRoleBadgeColor(user.role)}`}>
                    {user.role}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeColor(user.status)}`}>
                    {user.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {formatDate(user.registrationDate)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {user.lastLogin ? formatDate(user.lastLogin) : 'Never'}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {user.resourcesUploaded}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div className="relative inline-block text-left">
                    <button
                      onClick={() => onViewDetails(user.id)}
                      className="text-primary hover:text-primary/80 mr-4"
                    >
                      View
                    </button>
                    <div className="inline-block">
                      <select
                        onChange={(e) => {
                          const action = e.target.value;
                          e.target.value = '';
                          if (action === 'block') onBlockUser(user.id);
                          else if (action === 'suspend') onSuspendUser(user.id);
                          else if (action === 'activate') onActivateUser(user.id);
                          else if (action === 'changeRole') onChangeRole(user.id);
                        }}
                        className="text-sm border border-gray-300 rounded-md px-3 py-1 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-primary"
                        defaultValue=""
                      >
                        <option value="" disabled>Actions</option>
                        {user.status === 'ACTIVE' && (
                          <>
                            <option value="block">Block User</option>
                            <option value="suspend">Suspend User</option>
                          </>
                        )}
                        {(user.status === 'BLOCKED' || user.status === 'SUSPENDED') && (
                          <option value="activate">Activate User</option>
                        )}
                        <option value="changeRole">Change Role</option>
                      </select>
                    </div>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
