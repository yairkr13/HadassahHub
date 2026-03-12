package com.hadassahhub.backend.repository;

import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.entity.UserSuspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserSuspension entity.
 * Provides methods for managing user suspension records.
 */
@Repository
public interface UserSuspensionRepository extends JpaRepository<UserSuspension, Long> {

    /**
     * Find the active suspension for a user.
     * @param user The user to check
     * @return Optional containing the active suspension if it exists
     */
    Optional<UserSuspension> findByUserAndIsActiveTrue(User user);

    /**
     * Find all suspensions for a user (for history).
     * @param user The user
     * @return List of all suspensions for the user
     */
    List<UserSuspension> findByUserOrderBySuspendedAtDesc(User user);

    /**
     * Find all active suspensions that have expired.
     * Used by the scheduled task to auto-activate expired suspensions.
     * @param now The current timestamp
     * @return List of expired active suspensions
     */
    @Query("SELECT s FROM UserSuspension s WHERE s.isActive = true AND s.expiresAt < :now")
    List<UserSuspension> findExpiredActiveSuspensions(LocalDateTime now);
}
