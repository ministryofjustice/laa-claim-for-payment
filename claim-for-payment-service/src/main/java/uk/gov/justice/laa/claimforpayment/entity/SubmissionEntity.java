package uk.gov.justice.laa.claimforpayment.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.claimforpayment.repository.SubmissionEntityListener;

/** The entity class for claims. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SUBMISSIONS")
@EntityListeners(SubmissionEntityListener.class)
public class SubmissionEntity {

  @Id @GeneratedValue private UUID id;

  @Column(name = "provider_user_id")
  private UUID providerUserId;

  @Column(name = "friendly_id", unique = true, nullable = false, updatable = false)
  private String friendlyId;

  @Column private UUID providerOfficeId;

  @Column(name = "submission_type_code")
  private String submissionTypeCode;

  @Column(name = "submission_date")
  private LocalDateTime submissionDate;

  @Column(name = "submission_period_start_date")
  private LocalDateTime submissionPeriodStartDate;

  @Column(name = "submission_period_end_date")
  private LocalDateTime submissionPeriodEndDate;

  @Column(name = "schedule_id")
  private String scheduleId;

  @OneToMany(
      mappedBy = "submission",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Builder.Default
  private List<ClaimEntity> claims = new ArrayList<>();
}
