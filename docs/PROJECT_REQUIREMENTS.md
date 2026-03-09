HadassahHub – Product Requirements Document
1. Project Overview

HadassahHub is a web platform designed for students of Hadassah Academic College to share academic resources, explore courses, and make better decisions when planning their studies.

Currently, academic knowledge such as exam solutions, summaries, and course advice is scattered across WhatsApp groups and private Google Drive folders with limited accessibility.

HadassahHub aims to centralize this knowledge into a structured collaborative platform where students can contribute resources and benefit from shared community knowledge.

The platform will also provide course insights such as reviews, difficulty indicators, and study materials to improve transparency around courses.

Future versions may include intelligent course recommendation and study planning tools.

2. Target Users

Primary users:

Students of Hadassah Academic College.

User roles:

Guest – non-authenticated visitor
User – authenticated student with college email
Admin – moderator and system manager

3. Core Goals

The platform should:

• Provide a centralized course catalog
• Enable students to share academic resources
• Improve transparency around course difficulty and workload
• Help students plan their studies effectively
• Encourage collaboration and community knowledge sharing
• Allow searching and filtering of courses

4. System Features
4.1 Public Access (No Login Required)

Visitors can:

• Browse the course catalog
• Search courses
• Filter courses by year and type
• Open a course page
• View course descriptions
• Read course reviews and ratings
• View difficulty indicators

Visitors cannot:

• Upload resources
• Access restricted resources such as exam solutions

4.2 Authenticated Users

After creating an account using a college email address, users gain access to additional features.

Users can:

Access Resources

View course resources such as:

• Study summaries
• Past exams
• Exam solutions
• Helpful external links

Upload Resources

Users can contribute resources:

• Study summaries
• Past exams
• Exam solutions
• Helpful links

Uploaded resources may require admin approval before becoming public.

Course Reviews and Ratings

Users can rate courses and share insights.

Example metrics:

• Overall rating (1–5)
• Difficulty rating (1–5)
• Workload rating (1–5)
• Short review / advice

5. Course Structure

Courses are divided into two categories:

Required Computer Science Courses

These courses belong to a specific academic year.

Example:

Year 1
Year 2
Year 3

Elective Courses

Elective courses do not have a fixed academic year.

Types include:

• CS electives
• College-wide electives

6. Course Page Structure

Each course will have a dedicated page containing:

• Course name
• Credits (academic points)
• Course description
• Information about the course format
• Advice about who the course is suitable for
• Course reviews and ratings

The page will also contain learning resources such as:

• Past exams organized by year
• Homework assignments organized by year
• Study summaries
• Helpful links

Example resource structure:

Past Exams
2024
2023
2022

Homework
2024
2023

Summaries
Various summaries uploaded by users

7. Academic Credits (Future Feature)

Students typically need approximately 140 academic credits to complete their degree.

Future versions of the platform may include a Study Planner where students can:

• Plan courses for upcoming semesters
• Track accumulated credits
• Track required vs elective credits
• Ensure they meet graduation requirements

8. Administration

Admins can:

• Approve or reject uploaded resources
• Manage courses
• Moderate inappropriate content
• Monitor system activity

9. Technology Stack

Frontend

React
WebStorm IDE

Backend

Java
Spring Boot
Spring Security
REST API

Database

PostgreSQL

Infrastructure

Docker
Docker Compose

Authentication

JWT-based authentication

10. Language Support

Primary interface language:

Hebrew

Course materials may include English content.

Future versions may support multilingual interfaces.

11. Future Improvements

Possible future features:

• Study planner with credit tracking
• AI-based course recommendations
• Community-based grade statistics
• File uploads (instead of links only)
• Mobile-friendly UI
• Notifications for new resources

12. MVP Scope

The first version of the system will include:

• Course catalog
• Course pages
• Authentication using college email
• Resource links
• Course reviews and ratings
• Basic filtering and search

Advanced features such as study planning and AI recommendations will be added in later versions.