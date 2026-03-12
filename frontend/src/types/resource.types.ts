export interface Resource {
  id: number;
  title: string;
  type: ResourceType;
  url: string | null;
  academicYear?: string;
  examTerm?: string;
  status: ResourceStatus;
  rejectionReason?: string;
  // Flat fields from backend
  courseId?: number | null;
  courseName?: string | null;
  uploadedById?: number | null;
  uploaderName?: string | null;
  approvedById?: number | null;
  approverName?: string | null;
  approvedAt?: string;
  createdAt: string;
  updatedAt?: string;
  // File upload fields
  isFileUpload?: boolean;
  fileName?: string;
  fileSize?: number;
  mimeType?: string;
  // Visibility and permission flags
  isOwner?: boolean;
  canModerate?: boolean;
}

export interface CreateResourceRequest {
  courseId: number;
  title: string;
  type: ResourceType;
  url?: string; // Optional - either url or file must be provided
  academicYear?: string;
  examTerm?: string;
  file?: File; // Optional - for file uploads
}

export interface UpdateResourceRequest {
  title?: string;
  type?: ResourceType;
  url?: string;
  academicYear?: string;
  examTerm?: string;
}

export interface ResourceStats {
  totalResources: number;
  approvedResources: number;
  pendingResources: number;
  rejectedResources: number;
  resourcesByType: {
    [key in ResourceType]: number;
  };
}

export enum ResourceType {
  EXAM = 'EXAM',
  HOMEWORK = 'HOMEWORK',
  SUMMARY = 'SUMMARY',
  LINK = 'LINK'
}

export enum ResourceStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface ResourceFilters {
  type?: ResourceType;
  academicYear?: string;
  status?: ResourceStatus;
  search?: string;
}

// Display helpers
export const getResourceTypeDisplayName = (type: ResourceType): string => {
  switch (type) {
    case ResourceType.EXAM:
      return 'Exam';
    case ResourceType.HOMEWORK:
      return 'Homework';
    case ResourceType.SUMMARY:
      return 'Summary';
    case ResourceType.LINK:
      return 'Link';
    default:
      return 'Resource';
  }
};

export const getResourceStatusDisplayName = (status: ResourceStatus): string => {
  switch (status) {
    case ResourceStatus.PENDING:
      return 'Pending Review';
    case ResourceStatus.APPROVED:
      return 'Approved';
    case ResourceStatus.REJECTED:
      return 'Rejected';
    default:
      return 'Unknown';
  }
};

export const getResourceStatusColor = (status: ResourceStatus): string => {
  switch (status) {
    case ResourceStatus.PENDING:
      return 'bg-yellow-100 text-yellow-800';
    case ResourceStatus.APPROVED:
      return 'bg-green-100 text-green-800';
    case ResourceStatus.REJECTED:
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getResourceTypeColor = (type: ResourceType): string => {
  switch (type) {
    case ResourceType.EXAM:
      return 'bg-red-100 text-red-800';
    case ResourceType.HOMEWORK:
      return 'bg-blue-100 text-blue-800';
    case ResourceType.SUMMARY:
      return 'bg-purple-100 text-purple-800';
    case ResourceType.LINK:
      return 'bg-green-100 text-green-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};