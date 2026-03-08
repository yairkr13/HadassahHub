package com.hadassahhub.backend.enums;

/**
 * Enum representing different types of educational resources.
 * Currently supports URL-based resources with future extensibility for file uploads.
 */
public enum ResourceType {
    EXAM,      // Past exams (URLs for now, files in future)
    HOMEWORK,  // Homework assignments (URLs for now, files in future)  
    SUMMARY,   // Course summaries/notes (URLs for now, files in future)
    LINK       // External links (always URLs)
}