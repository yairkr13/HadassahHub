
# HadassahHub — Database Schema

## Overview

The database supports the core features of the HadassahHub platform.

Main capabilities supported:

- Course catalog
- Course offerings by semester
- Academic resources shared by students
- Course reviews and ratings
- Reputation / contribution system
- Community grade statistics
- Future course planning support

Database engine: PostgreSQL

The schema is designed to be:
- normalized
- scalable
- compatible with future features

---

# Core Design Principles

## 1. Separation Between Course and Offering

A course represents the academic subject.

A course offering represents when the course is taught.

Example:

Course: Data Structures

Offering:
Year: Y1
Semester: B

---

## 2. Required vs Elective Courses

Courses are divided into categories.

Only required Computer Science courses are tied to a specific study year.

Elective courses are not restricted to a specific year.

Course categories:

- CS_CORE – Required Computer Science course
- CS_ELECTIVE – Computer Science elective
- GENERAL_ELECTIVE – College-wide elective

---

## 3. Community Knowledge

Students can contribute:

- summaries
- past exams
- solutions
- useful links

These are stored as resources.

---

## 4. Moderation

Uploaded resources may require approval by admins.

---

# Tables

## users

Stores platform users.

Columns:

- id (primary key)
- email
- password_hash
- display_name
- role (USER / ADMIN)
- points_balance
- created_at

Purpose:

Users can upload resources, write course reviews, and rate resources.

---

## courses

Stores general course information.

Columns:

- id (primary key)
- code
- name_he
- name_en
- description_he
- description_en
- credits
- category
- recommended_year
- created_at

credits:
Number of academic credit points (נ"ז).

category values:

CS_CORE  
CS_ELECTIVE  
GENERAL_ELECTIVE

recommended_year values:

Y1  
Y2  
Y3  
NULL (for electives)

---

## course_offerings

Represents when a course is offered.

Columns:

- id (primary key)
- course_id (foreign key → courses.id)
- year_of_study
- semester
- created_at

year_of_study values:

Y1  
Y2  
Y3

semester values:

A – Semester A  
B – Semester B  
S – Summer

---

## resources

Stores study materials uploaded by users.

Columns:

- id (primary key)
- course_id (foreign key → courses.id)
- user_id (foreign key → users.id)
- title
- resource_kind
- url
- academic_year
- exam_term
- status
- approved_by
- approved_at
- created_at

resource_kind values:

SUMMARY  
PAST_EXAM  
SOLUTION  
HOMEWORK  
LINK

status values:

PENDING  
APPROVED  
REJECTED

---

## course_reviews

Stores course reviews written by users.

Columns:

- id (primary key)
- course_id
- user_id
- difficulty_rating
- workload_rating
- overall_rating
- comment
- created_at

Constraint:

Each user can review a course only once.

Unique constraint:

(user_id, course_id)

---

## resource_ratings

Stores ratings for resources.

Columns:

- id (primary key)
- resource_id
- user_id
- rating
- created_at

Unique constraint:

(user_id, resource_id)

---

## points_transactions

Tracks reputation system activity.

Columns:

- id
- user_id
- action
- delta
- reference_type
- reference_id
- created_at

Example actions:

UPLOAD_RESOURCE  
RATE_RESOURCE  
RESOURCE_APPROVED

---

## grade_stats

Stores historical grade statistics.

Columns:

- id
- course_id
- academic_year
- semester
- avg_grade
- median_grade
- pass_rate
- sample_size
- created_at

Purpose:

Provides insight into course difficulty.

---

# Relationships

users → resources (1:N)

users → course_reviews (1:N)

users → resource_ratings (1:N)

users → points_transactions (1:N)

courses → course_offerings (1:N)

courses → resources (1:N)

courses → course_reviews (1:N)

courses → grade_stats (1:N)

resources → resource_ratings (1:N)

---

# Entity Relationship Overview

User
│
├── Resources
├── Reviews
└── PointsTransactions

Course
│
├── CourseOfferings
├── Resources
├── Reviews
└── GradeStats

Resource
│
└── ResourceRatings

---

# Future Database Improvements

Possible future extensions:

- course prerequisites
- tagging system for resources
- file uploads instead of links
- notification system
- study planner (tracking 140 credits)
- AI-based course recommendation

The schema was designed so these features can be added without redesigning the database.
