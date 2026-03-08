package com.hadassahhub.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModerationStatsDTOTest {
    
    @Test
    void testModerationStatsDTO() {
        ModerationStatsDTO dto = new ModerationStatsDTO(5L, 15L, 3L);
        
        assertEquals(5L, dto.pendingCount());
        assertEquals(15L, dto.approvedCount());
        assertEquals(3L, dto.rejectedCount());
    }
    
    @Test
    void testGetTotalResources() {
        ModerationStatsDTO dto = new ModerationStatsDTO(5L, 15L, 3L);
        
        assertEquals(23L, dto.getTotalResources());
    }
    
    @Test
    void testHasPendingResources() {
        ModerationStatsDTO withPending = new ModerationStatsDTO(5L, 15L, 3L);
        assertTrue(withPending.hasPendingResources());
        
        ModerationStatsDTO noPending = new ModerationStatsDTO(0L, 15L, 3L);
        assertFalse(noPending.hasPendingResources());
    }
    
    @Test
    void testGetApprovalRate() {
        ModerationStatsDTO dto = new ModerationStatsDTO(5L, 15L, 3L);
        
        // 15 approved out of 18 moderated (15 + 3) = 83.33%
        assertEquals(83.33333333333334, dto.getApprovalRate(), 0.001);
    }
    
    @Test
    void testGetApprovalRate_NoModeratedResources() {
        ModerationStatsDTO dto = new ModerationStatsDTO(5L, 0L, 0L);
        
        assertEquals(0.0, dto.getApprovalRate());
    }
    
    @Test
    void testGetRejectionRate() {
        ModerationStatsDTO dto = new ModerationStatsDTO(5L, 15L, 3L);
        
        // 3 rejected out of 18 moderated (15 + 3) = 16.67%
        assertEquals(16.666666666666668, dto.getRejectionRate(), 0.001);
    }
    
    @Test
    void testGetRejectionRate_NoModeratedResources() {
        ModerationStatsDTO dto = new ModerationStatsDTO(5L, 0L, 0L);
        
        assertEquals(0.0, dto.getRejectionRate());
    }
    
    @Test
    void testEmpty() {
        ModerationStatsDTO dto = ModerationStatsDTO.empty();
        
        assertEquals(0L, dto.pendingCount());
        assertEquals(0L, dto.approvedCount());
        assertEquals(0L, dto.rejectedCount());
        assertEquals(0L, dto.getTotalResources());
        assertFalse(dto.hasPendingResources());
        assertEquals(0.0, dto.getApprovalRate());
        assertEquals(0.0, dto.getRejectionRate());
    }
    
    @Test
    void testApprovalRateEdgeCases() {
        // 100% approval rate
        ModerationStatsDTO allApproved = new ModerationStatsDTO(0L, 10L, 0L);
        assertEquals(100.0, allApproved.getApprovalRate());
        assertEquals(0.0, allApproved.getRejectionRate());
        
        // 100% rejection rate
        ModerationStatsDTO allRejected = new ModerationStatsDTO(0L, 0L, 10L);
        assertEquals(0.0, allRejected.getApprovalRate());
        assertEquals(100.0, allRejected.getRejectionRate());
        
        // 50/50 split
        ModerationStatsDTO balanced = new ModerationStatsDTO(0L, 5L, 5L);
        assertEquals(50.0, balanced.getApprovalRate());
        assertEquals(50.0, balanced.getRejectionRate());
    }
}