6.3.2026

HadassahHub - Project Development Summary
Project Overview

HadassahHub is a comprehensive academic resource sharing platform designed for computer science students.

The platform enables students to:

share course materials (exams, homework, summaries, links)

browse and search course resources

access course information

benefit from moderated community content

The system includes moderation, course catalog management, and secure user authentication.

1. Project Architecture
Backend Stack

Framework: Spring Boot 3.x (Java 21)

Database: PostgreSQL (production-ready) / H2 (testing environment)

Security: Spring Security with JWT authentication

ORM: Hibernate / Spring Data JPA

Build Tool: Maven

Testing: JUnit 5, Mockito, Spring Boot Test

Database

Testing environment: H2 in-memory database

Production-ready database: PostgreSQL

Schema management: Hibernate auto-DDL

Data seeding: Automated development data via DataSeeder

H2 is used primarily for testing environments while PostgreSQL is the intended production database.

Main Modules
backend/
├── config/          # Security, JWT, CORS configuration
├── controller/      # REST API endpoints
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── enums/           # Enumerations (UserRole, ResourceType, etc.)
├── exception/       # Custom exceptions and global handler
├── repository/      # Data access layer
├── service/         # Business logic layer
├── specification/   # JPA Criteria API specifications
└── seed/            # Development data seeding
2. Implemented Systems
Authentication System (JWT)

Features:

User registration with email domain validation

JWT-based authentication

Role-based access control

BCrypt password hashing

User profile retrieval

Allowed domains:

@edu.jmc.ac.il
@edu.hac.ac.il

Roles:

STUDENT
ADMIN
Course Catalog System

The course catalog provides structured academic course information.

Features:

Course metadata management

Course categorization

Course offerings by semester

Study year recommendations

Flexible filtering and search

Categories:

CS_CORE
CS_ELECTIVE
GENERAL_ELECTIVE
Resources System with Moderation

The resources system allows students to share learning materials while maintaining moderation control.

Supported resource types:

EXAM
HOMEWORK
SUMMARY
LINK

Core capabilities:

resource upload via external URLs

moderation workflow

academic year tracking

exam term tracking

filtering and searching

resource statistics

3. Database Entities
User
id
email
passwordHash
displayName
role
pointsBalance
createdAt
updatedAt
Course
id
name
description
category
credits
recommendedYear
CourseOffering
id
course
year
semester

Unique constraint:

(course, year, semester)
Resource
id
course
uploadedBy
title
type
url
academicYear
examTerm
status
rejectionReason
approvedBy
approvedAt
createdAt
updatedAt
Enumerations

UserRole

STUDENT
ADMIN

CourseCategory

CS_CORE
CS_ELECTIVE
GENERAL_ELECTIVE

StudyYear

Y1
Y2
Y3

Semester

A
B
S

ResourceType

EXAM
HOMEWORK
SUMMARY
LINK

ResourceStatus

PENDING
APPROVED
REJECTED
4. Main API Endpoints
Authentication
POST /api/auth/register
POST /api/auth/login
GET  /api/users/profile
Courses
GET  /api/courses
GET  /api/courses/{id}
GET  /api/courses/{id}/resources
GET  /api/courses/{id}/resources/stats
Resources (Student)
POST   /api/resources
GET    /api/resources/my-resources
GET    /api/resources/{id}
DELETE /api/resources/{id}
GET    /api/resources/course/{id}
Admin Moderation
GET  /api/resources/pending
POST /api/resources/{id}/approve
POST /api/resources/{id}/reject
POST /api/resources/{id}/reset
GET  /api/resources/moderation/stats
5. Security Model
JWT Authentication

Stateless authentication

Token contains user ID and role

Secure signing using HMAC-SHA256

Configurable expiration

Role-Based Access

STUDENT

upload resources

view approved resources

manage own uploads

ADMIN

moderate resources

approve / reject resources

access moderation endpoints

Resource Visibility Rules

PENDING

visible to uploader and admins only

APPROVED

visible to all users

REJECTED

visible to uploader with rejection reason

Ownership rules:

users can modify/delete only their resources

admins can manage any resource

6. Resource Workflow
Student uploads resource
        ↓
Resource status = PENDING
        ↓
Admin moderation
        ↓
APPROVED → visible on course page
REJECTED → visible only to uploader
7. Development Status
Fully Implemented Backend

JWT authentication

Course catalog

Resource system with moderation

filtering and search

role-based security

resource statistics

exception handling

CORS configuration

development data seeding

Technical Infrastructure

Spring Boot backend

PostgreSQL-ready database

RESTful API design

DTO-based API responses

JPA Specifications for dynamic queries

comprehensive unit and integration tests

8. Next Milestone: Frontend

Recommended stack:

React 18
TypeScript
React Router
React Query
Axios
Tailwind CSS or Material UI
Vite
Core Pages
Authentication
/login
/register
Course Catalog
/courses

Features:

filtering

search

course cards

Course Details
/courses/:id

Includes:

course information

resource list

resource filtering

statistics

Resource Management

User pages:

/my-resources
/upload

Admin pages:

/admin/moderation
Frontend Development Phases

Phase 1

Authentication
Navigation

Phase 2

Course catalog
Course details page

Phase 3

Resource upload
Resource management

Phase 4

Admin moderation dashboard

Phase 5

UI polish
performance improvements
Ready for Frontend Development

The backend is fully implemented and provides a stable API foundation.

All authentication, course management, and resource moderation features are operational and ready for integration with the frontend application.