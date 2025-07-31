package uk.gov.justice.laa.claimforpayment.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.claimforpayment.entity.SubmissionEntity;

/** Repository for managing submission entities. */
@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {

  // Find submissions by the provider user
  List<SubmissionEntity> findByProviderUserId(UUID providerUserId);
}
