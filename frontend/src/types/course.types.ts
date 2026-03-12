import { Resource, ResourceStats } from './resource.types';

export interface Course {
  id: number;
  name: string;
  description: string;
  category: CourseCategory;
  credits: number;
  recommendedYear?: number;
}

export interface CourseWithResources extends Course {
  resourceCount: number;
  approvedResourceCount: number;
}

export interface CourseDetail extends Course {
  resources: Resource[];
  resourceStats: ResourceStats;
}

// Backend enum mapping
export enum CourseCategory {
  CS_CORE = 'CS_CORE',
  CS_ELECTIVE = 'CS_ELECTIVE', 
  GENERAL_ELECTIVE = 'GENERAL_ELECTIVE'
}

// Frontend display mapping
export enum CourseCategoryDisplay {
  CS_MANDATORY = 'CS_MANDATORY',
  CS_ELECTIVE = 'CS_ELECTIVE',
  COLLEGE_GENERAL = 'COLLEGE_GENERAL'
}

export interface CourseFilters {
  search?: string;
  category?: CourseCategoryDisplay;
  year?: number;
}

// Helper function to map backend categories to frontend display
export const mapBackendCategoryToDisplay = (backendCategory: CourseCategory): CourseCategoryDisplay => {
  switch (backendCategory) {
    case CourseCategory.CS_CORE:
      return CourseCategoryDisplay.CS_MANDATORY;
    case CourseCategory.CS_ELECTIVE:
      return CourseCategoryDisplay.CS_ELECTIVE;
    case CourseCategory.GENERAL_ELECTIVE:
      return CourseCategoryDisplay.COLLEGE_GENERAL;
    default:
      return CourseCategoryDisplay.CS_MANDATORY;
  }
};

// Helper function to map frontend display to backend categories
export const mapDisplayCategoryToBackend = (displayCategory: CourseCategoryDisplay): CourseCategory => {
  switch (displayCategory) {
    case CourseCategoryDisplay.CS_MANDATORY:
      return CourseCategory.CS_CORE;
    case CourseCategoryDisplay.CS_ELECTIVE:
      return CourseCategory.CS_ELECTIVE;
    case CourseCategoryDisplay.COLLEGE_GENERAL:
      return CourseCategory.GENERAL_ELECTIVE;
    default:
      return CourseCategory.CS_CORE;
  }
};