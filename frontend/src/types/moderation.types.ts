export interface ModerationStats {
  pendingCount: number;
  approvedCount: number;
  rejectedCount: number;
}

export interface RejectResourceRequest {
  reason: string;
}

export interface ModerationAction {
  type: 'approve' | 'reject' | 'reset';
  resourceId: number;
  reason?: string;
}