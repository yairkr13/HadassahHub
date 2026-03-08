# HadassahHub -- System Architecture

## Overview

HadassahHub is a full‑stack web platform designed for students at
Hadassah Academic College to share academic resources and explore course
information.

The system is composed of three main layers:

-   Frontend (React)
-   Backend (Spring Boot REST API)
-   Database (PostgreSQL)

Infrastructure and services are managed using Docker and Docker Compose.

------------------------------------------------------------------------

## High Level Architecture

User Browser \| v React Frontend \| v Spring Boot REST API \| v
PostgreSQL Database

Authentication is handled using JWT tokens issued by the backend.

------------------------------------------------------------------------

## Components

### Frontend

Technology: - React - WebStorm IDE

Responsibilities: - UI rendering - Course search and filtering - Forms
for login/register - Upload resources - Display statistics and reviews

------------------------------------------------------------------------

### Backend

Technology: - Java - Spring Boot - Spring Security - REST API

Responsibilities: - Business logic - Authentication (JWT) - Resource
management - Course management - Reputation system - Admin approval
system

------------------------------------------------------------------------

### Database

Technology: - PostgreSQL

Responsibilities: - Store users - Store courses - Store resources -
Store reviews and ratings - Store reputation points - Store grade
statistics

------------------------------------------------------------------------

### Infrastructure

Technology: - Docker - Docker Compose

Services: - PostgreSQL container - Backend container (later stage) -
Frontend container (later stage)

------------------------------------------------------------------------

## Authentication Flow

1.  User registers or logs in.
2.  Backend validates credentials.
3.  Backend issues JWT token.
4.  Frontend stores token.
5.  Token is sent with every protected request.

------------------------------------------------------------------------

## Deployment Plan (Future)

Frontend: - Vercel / Netlify

Backend: - Render / Railway / AWS

Database: - Managed PostgreSQL or Docker container

------------------------------------------------------------------------

## Future Architecture Extensions

Possible improvements:

-   File storage service (AWS S3 / Cloud storage)
-   AI recommendation service
-   Notification service
-   Mobile support
