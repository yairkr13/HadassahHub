# Requirements Document

## Introduction

The Admin Moderation System enables administrators and moderators to review, approve, reject, and manage user-submitted resources. This system ensures quality control and prevents inappropriate content from being published, maintaining the integrity of the educational resource platform.

## Requirements

### Requirement 1

**User Story:** As a moderator, I want to view all pending resources in a queue so that I can efficiently review submissions awaiting approval.

#### Acceptance Criteria

1. WHEN a moderator is authenticated THEN the system SHALL display a pending resources queue
2. WHEN viewing the queue THEN the system SHALL show resource title, type, course, uploader, and upload date
3. WHEN viewing the queue THEN the system SHALL display file information for file uploads
4. WHEN viewing the queue THEN the system SHALL show academic year and exam term when applicable
5. WHEN the queue is empty THEN the system SHALL display an appropriate message
6. WHEN viewing the queue THEN the system SHALL provide action buttons for each resource

### Requirement 2

**User Story:** As a moderator, I want to approve resources so that quality content becomes visible to all users.

#### Acceptance Criteria

1. WHEN reviewing a resource THEN the system SHALL provide an approve button
2. WHEN approving a resource THEN the system SHALL change status to APPROVED
3. WHEN approving a resource THEN the system SHALL record moderator ID and timestamp
4. WHEN approving a resource THEN the system SHALL make the resource visible to all users
5. WHEN approving a resource THEN the system SHALL create an audit log entry
6. WHEN approving a resource THEN the system SHALL display a success notification

### Requirement 3

**User Story:** As a moderator, I want to reject resources with a reason so that uploaders understand why their submission was not approved.

#### Acceptance Criteria

1. WHEN reviewing a resource THEN the system SHALL provide a reject button
2. WHEN rejecting a resource THEN the system SHALL require a rejection reason
3. WHEN rejecting a resource THEN the system SHALL change status to REJECTED
4. WHEN rejecting a resource THEN the system SHALL record moderator ID, timestamp, and reason
5. WHEN rejecting a resource THEN the system SHALL make the resource visible only to uploader and moderators
6. WHEN rejecting a resource THEN the system SHALL display the rejection reason to the uploader
7. WHEN rejecting a resource THEN the system SHALL create an audit log entry

### Requirement 4

**User Story:** As a moderator, I want to reset rejected resources back to pending so that I can give uploaders a second chance after improvements.

#### Acceptance Criteria

1. WHEN viewing a rejected resource THEN the system SHALL provide a reset button
2. WHEN resetting a resource THEN the system SHALL change status back to PENDING
3. WHEN resetting a resource THEN the system SHALL preserve previous rejection reason in history
4. WHEN resetting a resource THEN the system SHALL return the resource to the moderation queue
5. WHEN resetting a resource THEN the system SHALL create an audit log entry

### Requirement 5

**User Story:** As a moderator, I want to delete resources permanently so that I can remove inappropriate or duplicate content.

#### Acceptance Criteria

1. WHEN reviewing a resource THEN the system SHALL provide a delete button
2. WHEN deleting a resource THEN the system SHALL require confirmation
3. WHEN deleting a file upload resource THEN the system SHALL also delete the associated file
4. WHEN deleting a resource THEN the system SHALL remove it from the database
5. WHEN deleting a resource THEN the system SHALL create an audit log entry
6. WHEN deleting a resource THEN the system SHALL display a success notification

### Requirement 6

**User Story:** As a moderator, I want to view moderation statistics so that I can track the volume and status of resources in the system.

#### Acceptance Criteria

1. WHEN viewing the moderation dashboard THEN the system SHALL display pending resources count
2. WHEN viewing the moderation dashboard THEN the system SHALL display approved resources count
3. WHEN viewing the moderation dashboard THEN the system SHALL display rejected resources count
4. WHEN viewing the moderation dashboard THEN the system SHALL display total resources count
5. WHEN statistics change THEN the system SHALL update the displayed counts
6. WHEN viewing statistics THEN the system SHALL provide visual indicators for high pending counts

### Requirement 7

**User Story:** As a moderator, I want to use common rejection reasons so that I can quickly reject resources with standard feedback.

#### Acceptance Criteria

1. WHEN rejecting a resource THEN the system SHALL provide a dropdown of common rejection reasons
2. WHEN selecting a common reason THEN the system SHALL populate the rejection reason field
3. WHEN using a common reason THEN the system SHALL allow custom text addition
4. WHEN common reasons are available THEN the system SHALL include options for inappropriate content, low quality, duplicates, wrong category, copyright violations, spam, and corrupted files
5. WHEN rejecting THEN the system SHALL allow entering a completely custom reason
