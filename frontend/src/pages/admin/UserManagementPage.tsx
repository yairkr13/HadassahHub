import React, { useState } from 'react';
import {
  UserFilters,
  UserTable,
  UserDetailsModal,
  BlockUserModal,
  SuspendUserModal,
  ChangeRoleModal,
} from '@/components/admin';
import { Pagination } from '@/components/common';
import {
  useAdminUsers,
  useUserDetails,
  useBlockUser,
  useSuspendUser,
  useActivateUser,
  useChangeUserRole,
} from '@/hooks/useAdminUsers';
import { UserFilters as UserFiltersType, AdminUser, UserRole } from '@/types/admin-user.types';

export const UserManagementPage: React.FC = () => {
  const [filters, setFilters] = useState<UserFiltersType>({
    page: 0,
    size: 20,
    sortBy: 'createdAt',
    sortDirection: 'desc',
  });

  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [detailsModalOpen, setDetailsModalOpen] = useState(false);
  const [blockModalOpen, setBlockModalOpen] = useState(false);
  const [suspendModalOpen, setSuspendModalOpen] = useState(false);
  const [changeRoleModalOpen, setChangeRoleModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<AdminUser | null>(null);

  const [successMessage, setSuccessMessage] = useState<string>('');
  const [errorMessage, setErrorMessage] = useState<string>('');

  // Queries
  const { data: usersData, isLoading: usersLoading } = useAdminUsers(filters);
  const { data: userDetails, isLoading: detailsLoading } = useUserDetails(
    detailsModalOpen ? selectedUserId : null
  );

  // Mutations
  const blockMutation = useBlockUser();
  const suspendMutation = useSuspendUser();
  const activateMutation = useActivateUser();
  const changeRoleMutation = useChangeUserRole();

  const showMessage = (message: string, isError = false) => {
    if (isError) {
      setErrorMessage(message);
      setSuccessMessage('');
    } else {
      setSuccessMessage(message);
      setErrorMessage('');
    }

    setTimeout(() => {
      setSuccessMessage('');
      setErrorMessage('');
    }, 5000);
  };

  const handleViewDetails = (userId: number) => {
    setSelectedUserId(userId);
    setDetailsModalOpen(true);
  };

  const handleBlockUser = (userId: number) => {
    const user = usersData?.content.find((u) => u.id === userId);
    if (user) {
      setSelectedUser(user);
      setSelectedUserId(userId);
      setBlockModalOpen(true);
    }
  };

  const handleSuspendUser = (userId: number) => {
    const user = usersData?.content.find((u) => u.id === userId);
    if (user) {
      setSelectedUser(user);
      setSelectedUserId(userId);
      setSuspendModalOpen(true);
    }
  };

  const handleActivateUser = async (userId: number) => {
    try {
      await activateMutation.mutateAsync(userId);
      showMessage('User activated successfully!');
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to activate user', true);
    }
  };

  const handleChangeRole = (userId: number) => {
    const user = usersData?.content.find((u) => u.id === userId);
    if (user) {
      setSelectedUser(user);
      setSelectedUserId(userId);
      setChangeRoleModalOpen(true);
    }
  };

  const handleBlockConfirm = async (reason: string) => {
    if (!selectedUserId) return;

    try {
      await blockMutation.mutateAsync({
        userId: selectedUserId,
        request: { reason },
      });
      showMessage('User blocked successfully!');
      setBlockModalOpen(false);
      setSelectedUser(null);
      setSelectedUserId(null);
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to block user', true);
    }
  };

  const handleSuspendConfirm = async (reason: string, expiresAt: string) => {
    if (!selectedUserId) return;

    try {
      await suspendMutation.mutateAsync({
        userId: selectedUserId,
        request: { reason, expiresAt },
      });
      showMessage('User suspended successfully!');
      setSuspendModalOpen(false);
      setSelectedUser(null);
      setSelectedUserId(null);
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to suspend user', true);
    }
  };

  const handleChangeRoleConfirm = async (newRole: UserRole, reason?: string) => {
    if (!selectedUserId) return;

    try {
      await changeRoleMutation.mutateAsync({
        userId: selectedUserId,
        request: { newRole, reason },
      });
      showMessage('User role changed successfully!');
      setChangeRoleModalOpen(false);
      setSelectedUser(null);
      setSelectedUserId(null);
    } catch (error: any) {
      showMessage(error.response?.data?.message || 'Failed to change user role', true);
    }
  };

  const handlePageChange = (page: number) => {
    setFilters({ ...filters, page });
  };

  const isAnyMutationLoading =
    blockMutation.isPending ||
    suspendMutation.isPending ||
    activateMutation.isPending ||
    changeRoleMutation.isPending;

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">User Management</h1>
          <p className="text-text-secondary">
            Manage user accounts, roles, and permissions
          </p>
        </div>

        {/* Success/Error Messages */}
        {successMessage && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <div className="flex items-center">
              <svg
                className="w-5 h-5 text-green-600 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M5 13l4 4L19 7"
                />
              </svg>
              <p className="text-green-800">{successMessage}</p>
            </div>
          </div>
        )}

        {errorMessage && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
            <div className="flex items-center">
              <svg
                className="w-5 h-5 text-red-600 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"
                />
              </svg>
              <p className="text-red-800">{errorMessage}</p>
            </div>
          </div>
        )}

        {/* Filters */}
        <UserFilters filters={filters} onFilterChange={setFilters} />

        {/* Users Table */}
        <UserTable
          users={usersData?.content || []}
          isLoading={usersLoading || isAnyMutationLoading}
          onViewDetails={handleViewDetails}
          onBlockUser={handleBlockUser}
          onSuspendUser={handleSuspendUser}
          onActivateUser={handleActivateUser}
          onChangeRole={handleChangeRole}
        />

        {/* Pagination */}
        {usersData && usersData.totalPages > 1 && (
          <div className="mt-6">
            <Pagination
              currentPage={usersData.number}
              totalPages={usersData.totalPages}
              onPageChange={handlePageChange}
              totalElements={usersData.totalElements}
              pageSize={usersData.size}
            />
          </div>
        )}

        {/* Modals */}
        <UserDetailsModal
          isOpen={detailsModalOpen}
          onClose={() => {
            setDetailsModalOpen(false);
            setSelectedUserId(null);
          }}
          user={userDetails || null}
          isLoading={detailsLoading}
        />

        <BlockUserModal
          isOpen={blockModalOpen}
          onClose={() => {
            setBlockModalOpen(false);
            setSelectedUser(null);
            setSelectedUserId(null);
          }}
          onConfirm={handleBlockConfirm}
          user={selectedUser}
          isLoading={blockMutation.isPending}
        />

        <SuspendUserModal
          isOpen={suspendModalOpen}
          onClose={() => {
            setSuspendModalOpen(false);
            setSelectedUser(null);
            setSelectedUserId(null);
          }}
          onConfirm={handleSuspendConfirm}
          user={selectedUser}
          isLoading={suspendMutation.isPending}
        />

        <ChangeRoleModal
          isOpen={changeRoleModalOpen}
          onClose={() => {
            setChangeRoleModalOpen(false);
            setSelectedUser(null);
            setSelectedUserId(null);
          }}
          onConfirm={handleChangeRoleConfirm}
          user={selectedUser}
          isLoading={changeRoleMutation.isPending}
        />
      </div>
    </div>
  );
};
