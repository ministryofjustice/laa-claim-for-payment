package uk.gov.justice.laa.claimforpayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;

/**
 * Repository for managing item entities.
 */
@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {
}