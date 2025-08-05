package uk.gov.justice.laa.claimforpayment.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.claimforpayment.entity.SubmissionEntity;

/** Repository for managing submission entities. */
@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {

  // Find submissions by the provider user
  List<SubmissionEntity> findByProviderUserId(UUID providerUserId);

  // Include claim value totals for all
  @Query(
      """
          SELECT s.id, COALESCE(SUM(c.claimed), 0)
          FROM SubmissionEntity s
          LEFT JOIN s.claims c
          GROUP BY s.id
      """)
  List<Object[]> findSubmissionTotals();

  // include claim value totals for one submission
  @Query(
      """
          SELECT COALESCE(SUM(c.claimed), 0)
          FROM SubmissionEntity s
          LEFT JOIN s.claims c
          WHERE s.id = :id
      """)
  BigDecimal findSubmissionTotalById(@Param("id") UUID id);
}
