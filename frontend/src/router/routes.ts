export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  COURSES: '/courses',
  COURSE_DETAIL: '/courses/:id',
  COURSE_UPLOAD: '/courses/:id/upload',
  MY_RESOURCES: '/my-resources',
  ADMIN_MODERATION: '/admin/moderation',
} as const;

export type RouteKey = keyof typeof ROUTES;
export type RoutePath = typeof ROUTES[RouteKey];