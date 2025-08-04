package uk.gov.justice.laa.claimforpayment.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PrePersist;
import uk.gov.justice.laa.claimforpayment.entity.SubmissionEntity;

/** Helper. */
public class SubmissionEntityListener {

  /**
   * helper.
   *
   * @param submission the submission entity
   */
  @PrePersist
  public void setFriendlyId(SubmissionEntity submission) {
    if (submission.getFriendlyId() == null) {
      // Grab the EntityManager from Spring via a helper
      EntityManager em = BeanUtil.getBean(EntityManager.class);

      Long seqValue =
          ((Number)
                  em.createNativeQuery("SELECT nextval('submission_friendly_id_seq')")
                      .getSingleResult())
              .longValue();

      submission.setFriendlyId(String.format("LAA-%03d", seqValue));
    }
  }
}
