
# HadassahHub - Project Context (Updated)

## Project Overview
HadassahHub is an academic resource-sharing platform for computer science students.
Students can upload, browse, and manage study materials such as exams, homework,
summaries, and useful links. The platform includes moderation and role-based access.

## Backend Stack
- Spring Boot 3.x (Java 21)
- PostgreSQL (production) / H2 (development)
- Spring Security with JWT
- Hibernate / JPA
- Maven

## Core Entities
User
- id
- email
- passwordHash
- displayName
- role
- createdAt

Course
- id
- name
- description
- category
- credits

CourseOffering
- course
- year
- semester

Resource
- id
- course
- uploadedBy
- title
- type
- url
- status
- createdAt

## Roles
STUDENT
ADMIN

## Resource Status
PENDING
APPROVED
REJECTED

## Core API Areas
/auth
/users
/courses
/resources
/admin/moderation

## Frontend Stack
- React 18
- TypeScript
- Vite
- React Router
- React Query
- Axios
- TailwindCSS

## Core Pages
/
/login
/register
/courses
/courses/:id
/my-resources
/upload
/admin/moderation

## Homepage Behavior
The homepage is public and explains the platform.

Once a user logs in:
they are redirected to /courses.

The navigation bar changes to show:
Courses
My Resources
Upload
Profile
Logout

Admins also see:
Admin Moderation
