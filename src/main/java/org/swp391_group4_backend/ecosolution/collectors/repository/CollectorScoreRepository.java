package org.swp391_group4_backend.ecosolution.collectors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorScore;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectorScoreRepository extends JpaRepository<CollectorScore, UUID> {

  // Find score by collector ID
  Optional<CollectorScore> findByCollectorId(UUID collectorId);

  // Find collectors with high reliability scores
  @Query("SELECT cs FROM CollectorScore cs WHERE cs.reliabilityScore >= :minScore " +
         "ORDER BY cs.reliabilityScore DESC")
  List<CollectorScore> findByReliabilityScoreGreaterThanEqual(@Param("minScore") BigDecimal minScore);

  // Find top N collectors by reliability score
  @Query("SELECT cs FROM CollectorScore cs ORDER BY cs.reliabilityScore DESC")
  List<CollectorScore> findTopCollectorsByReliability(Pageable pageable);

  // Find collectors with low complaint rates
  @Query("SELECT cs FROM CollectorScore cs WHERE cs.complaintRate <= :maxRate " +
         "ORDER BY cs.complaintRate ASC")
  List<CollectorScore> findByComplaintRateLessThanEqual(@Param("maxRate") BigDecimal maxRate);

  // Find collectors with high completion rates
  @Query("SELECT cs FROM CollectorScore cs WHERE cs.completionRate >= :minRate " +
         "ORDER BY cs.completionRate DESC")
  List<CollectorScore> findByCompletionRateGreaterThanEqual(@Param("minRate") BigDecimal minRate);

  // Find collectors needing attention (low scores)
  @Query("SELECT cs FROM CollectorScore cs WHERE cs.reliabilityScore < :threshold " +
         "OR cs.complaintRate > :maxComplaintRate")
  List<CollectorScore> findCollectorsNeedingAttention(
      @Param("threshold") BigDecimal threshold,
      @Param("maxComplaintRate") BigDecimal maxComplaintRate
  );

  // Calculate average reliability score
  @Query("SELECT AVG(cs.reliabilityScore) FROM CollectorScore cs")
  BigDecimal calculateAverageReliabilityScore();
}
