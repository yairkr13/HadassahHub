export interface ModerationStats {
  totalPending: number;
  totalApproved: number;
  totalRejected: number;
  approvedToday: number;
  rejectedToday: number;
}

export interface RejectResourceRequest {
  reason: string;
}

export interface ModerationAction {
  type: 'approve' | 'reject' | 'reset';
  resourceId: number;
  reason?: string;
}