package com.hadassahhub.backend.dto;

/**
 * Record containing metadata about a stored file.
 * Used internally by FileStorageService to return file information after storage.
 */
public record FileMetadata(
    String originalFileName,
    String storedFileName,
    String filePath,
    long fileSize,
    String mimeType
) {}
