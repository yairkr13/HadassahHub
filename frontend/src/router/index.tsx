import React, { Suspense } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import { MainLayout } from '@/layouts';
import { ProtectedRoute } from './ProtectedRoute';
import { AuthRedirect } from './AuthRedirect';
import { 
  HomePage, 
  CoursesPage,
  CourseDetailPage,
  MyResourcesPage,
  UploadResourcePage,
  LoginPage, 
  RegisterPage 
} from '@/pages';
import { ROUTES } from './routes';

// Lazy load admin pages to reduce main bundle size
const ModerationPage = React.lazy(() => 
  import('@/pages/admin/ModerationPage').then(module => ({ 
    default: module.ModerationPage 
  }))
);

const PendingResourcesPage = React.lazy(() =>
  import('@/pages/admin/PendingResourcesPage').then(module => ({
    default: module.PendingResourcesPage
  }))
);

const UserManagementPage = React.lazy(() =>
  import('@/pages/admin/UserManagementPage').then(module => ({
    default: module.UserManagementPage
  }))
);

// Loading component for lazy-loaded routes
const LazyLoadingFallback = () => (
  <div className="min-h-screen bg-background-soft flex items-center justify-center">
    <div className="text-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
      <p className="text-text-secondary">Loading...</p>
    </div>
  </div>
);

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: ROUTES.LOGIN,
        element: (
          <AuthRedirect>
            <LoginPage />
          </AuthRedirect>
        ),
      },
      {
        path: ROUTES.REGISTER,
        element: (
          <AuthRedirect>
            <RegisterPage />
          </AuthRedirect>
        ),
      },
      {
        path: ROUTES.COURSES,
        element: (
          <ProtectedRoute>
            <CoursesPage />
          </ProtectedRoute>
        ),
      },
      {
        path: ROUTES.COURSE_DETAIL,
        element: (
          <ProtectedRoute>
            <CourseDetailPage />
          </ProtectedRoute>
        ),
      },
      {
        path: ROUTES.COURSE_UPLOAD,
        element: (
          <ProtectedRoute>
            <UploadResourcePage />
          </ProtectedRoute>
        ),
      },
      {
        path: ROUTES.MY_RESOURCES,
        element: (
          <ProtectedRoute>
            <MyResourcesPage />
          </ProtectedRoute>
        ),
      },
      {
        path: ROUTES.ADMIN_MODERATION,
        element: (
          <ProtectedRoute adminOnly={true}>
            <Suspense fallback={<LazyLoadingFallback />}>
              <ModerationPage />
            </Suspense>
          </ProtectedRoute>
        ),
      },
      {
        path: ROUTES.ADMIN_PENDING_RESOURCES,
        element: (
          <ProtectedRoute adminOnly={true}>
            <Suspense fallback={<LazyLoadingFallback />}>
              <PendingResourcesPage />
            </Suspense>
          </ProtectedRoute>
        ),
      },
      {
        path: ROUTES.ADMIN_USER_MANAGEMENT,
        element: (
          <ProtectedRoute adminOnly={true}>
            <Suspense fallback={<LazyLoadingFallback />}>
              <UserManagementPage />
            </Suspense>
          </ProtectedRoute>
        ),
      },
    ],
  },
]);

export * from './routes';
export * from './ProtectedRoute';
export * from './AuthRedirect';