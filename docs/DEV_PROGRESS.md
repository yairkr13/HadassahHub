HadassahHub – Development Progress

Date: 6.3.2026

This document summarizes the current technical progress of the HadassahHub backend project.
It is intended to help quickly resume development in the future.

Current Project Status

The backend architecture is implemented using Spring Boot and PostgreSQL.

The Course Catalog MVP backend foundations are complete and operational.

The Authentication System is fully implemented and tested.

The system supports course catalog browsing, filtering, user authentication, and structured API responses.

Implemented Infrastructure
Backend

Java Spring Boot project initialized.

Architecture structure:

Controller layer

Service layer

Repository layer

DTO layer

Exception handling layer

This layered architecture separates responsibilities and keeps the codebase maintainable.

Database

PostgreSQL database is running via Docker.

Containers:

PostgreSQL

pgAdmin

Docker Compose is used to manage the services.

Database Connection

Spring Boot is connected to PostgreSQL using:

JPA / Hibernate

Hikari connection pool

Database schema updates are enabled via:

spring.jpa.hibernate.ddl-auto=update
Current Data Model
Course

Represents a course in the catalog.

Fields:

id

name

description

category (CourseCategory enum: CS_CORE, CS_ELECTIVE, GENERAL_ELECTIVE)

credits (Integer)

recommendedYear (StudyYear enum: Y1, Y2, Y3 — nullable for electives)

Required CS courses include a recommended study year.
Elective courses may have a null recommended year.

CourseOffering

Represents when a course is offered.

Fields:

id

course_id

year (Y1 / Y2 / Y3)

semester (A / B / S)

This allows courses to appear in multiple semesters.

Backend Architecture Implemented
Repository Layer

Repositories extend Spring Data JPA.

Implemented repositories:

CourseRepository
Extends JpaRepository and JpaSpecificationExecutor for flexible filtering.

CourseOfferingRepository

CourseOffering queries:

findByYear()

findBySemester()

findByYearAndSemester()

Course queries use JPA Specifications for dynamic filtering.

Service Layer
CourseService

Implemented methods:

findCourses()
Supports search, category, and year filtering.

findCourseById()
Returns Optional<CourseDTO>.

toDTO()
Maps entities to DTO objects.

Uses CourseSpecifications for flexible filtering.

CourseOfferingService

Responsible for:

business logic

filtering by year and semester

mapping entities to DTO objects

DTO Layer

DTOs prevent exposing database entities directly.

CourseDTO

Fields:

id

name

description

category

credits

recommendedYear

CourseOfferingDTO

Fields:

offeringId

year

semester

courseId

courseName

category

description

Controller Layer
CourseController

Endpoints:

GET /api/courses
Supports search, category, and year query parameters.

GET /api/courses/{id}
Returns a specific course or 404.

CourseOfferingController

Endpoint:

GET /api/offerings

Supports:

year filtering

semester filtering

Example calls:

/api/courses
/api/courses?search=algorithm
/api/courses?category=CS_CORE
/api/courses?year=Y1
/api/courses?search=data&category=CS_CORE&year=Y1
/api/courses/1

/api/offerings
/api/offerings?year=Y1
/api/offerings?semester=A
/api/offerings?year=Y1&semester=B
Exception Handling

GlobalExceptionHandler implemented.

Handles:

MethodArgumentTypeMismatchException
Returns HTTP 400 for invalid enum values.

EntityNotFoundException
Returns HTTP 404 when a course is not found.

Responses use a consistent format via the ErrorResponse DTO.

Specifications Layer

CourseSpecifications utility class provides:

hasNameContaining() — case-insensitive search

hasCategory() — exact category match

hasRecommendedYear() — exact year match

Supports dynamic query building with AND logic.

Enums

Enums implemented:

StudyYear

Y1

Y2

Y3

Semester

A

B

S

CourseCategory

CS_CORE

CS_ELECTIVE

GENERAL_ELECTIVE

Docker Infrastructure

Docker containers currently running:

PostgreSQL

pgAdmin

Ports:

PostgreSQL → 5432
pgAdmin → 5050

Verified System Behavior

The backend API successfully:

connects to the database

reads and filters courses by search, category, and year

reads course offerings and filters by year and semester

returns DTO-based responses

handles errors with a consistent format

supports enum-based filtering

The Course Catalog MVP is ready for testing.

Data Seeding

DataSeeder populates the database with:

CS_CORE courses (with recommended years Y1, Y2)

CS_ELECTIVE courses (no recommended year)

GENERAL_ELECTIVE courses (no recommended year)

CourseOffering entries for multiple year/semester combinations

The dataset supports all filtering scenarios.

Completed Features
Course Catalog MVP Backend Foundations

Implemented:

Course entity with proper enums and fields

CourseCategory enum

CourseDTO for API responses

CourseSpecifications for flexible filtering

CourseRepository with JpaSpecificationExecutor

CourseService with business logic

CourseController with filtering support

ErrorResponse and GlobalExceptionHandler

DataSeeder with test data

Authentication System (COMPLETED)

Implemented:

User entity with role-based access control

JWT authentication and authorization

User registration with domain validation

User login with credential verification

JWT token generation and validation

Security configuration with protected endpoints

Role-based access (STUDENT / ADMIN)

Registration restricted to college domains:

@edu.jmc.ac.il

@edu.hac.ac.il

Password encryption using BCrypt

JWT authentication filter for request processing

API Endpoints

Course Catalog:

GET /api/courses

GET /api/courses/{id}

GET /api/offerings

Authentication:

POST /api/auth/register

POST /api/auth/login

User Management:

GET /api/users/me (authenticated)

All endpoints support filtering where relevant.

Testing Status

Testing infrastructure implemented and fully operational.

Current status:

Unit tests for CourseService — implemented and passing

Unit tests for AuthService — implemented and passing

Unit tests for JwtService — implemented and passing

Unit tests for UserService — implemented and passing

Integration tests for AuthController — implemented and passing

Integration tests for UserController — implemented and passing

H2 database configuration for tests resolved

All authentication integration tests passing successfully

Next Planned Features
Resources System (Next Milestone)

Users will be able to upload and manage course-related resources.

Resource entity fields:

course_id

user_id

type

url

year

approved

Resource types:

EXAM

HOMEWORK

SUMMARY

LINK

Enhanced Course Details Page

Richer course information display

Integration with Resources and Reviews systems

Course-specific resource listings

Course review summaries and statistics

Reviews System

Users will be able to review courses.

Fields:

course_id

user_id

rating

difficulty

workload

comment

Future Development

Planned additional modules:

Study planner (140 credit tracking)

Grade statistics

AI course recommendation

Reputation system

Notes

The Course Catalog MVP backend foundations are complete and functional.

The system follows a proper layered architecture with DTOs, service layers, and flexible filtering.

The backend is ready for frontend integration and further feature development.

Next focus: Authentication system implementation.