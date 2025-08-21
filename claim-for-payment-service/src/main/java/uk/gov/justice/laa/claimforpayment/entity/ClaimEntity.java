package uk.gov.justice.laa.claimforpayment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The entity class for claims. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLAIMS")
public class ClaimEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String ufn;

  private String client;

  private String category;

  private LocalDate concluded;

  @Column(name = "provider_user_id")
  private UUID providerUserId;

  @Column(name = "fee_type")
  private String feeType;

  @Column(name = "claimed", nullable = false, precision = 10, scale = 2)
  private BigDecimal claimed;

  @Column(name = "submission_id")
  private UUID submissionId;
}
