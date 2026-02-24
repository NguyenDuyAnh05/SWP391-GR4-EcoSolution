package org.swp391_group4_backend.ecosolution.fraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudSignal;
import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FraudSignalRepository extends JpaRepository<FraudSignal, UUID> {

  // Find fraud signals by citizen
  List<FraudSignal> findByCitizenId(UUID citizenId);

  // Find fraud signals by report
  List<FraudSignal> findByReportId(UUID reportId);

  // Find fraud signals by type
  List<FraudSignal> findByType(FraudType type);

  // Find high-risk fraud signals (score above threshold)
  @Query("SELECT f FROM FraudSignal f WHERE f.score >= :minScore ORDER BY f.score DESC")
  List<FraudSignal> findHighRiskSignals(@Param("minScore") Integer minScore);

  // Find fraud signals created within date range
  List<FraudSignal> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Calculate total fraud score for a citizen
  @Query("SELECT SUM(f.score) FROM FraudSignal f WHERE f.citizen.id = :citizenId")
  Integer calculateTotalFraudScoreByCitizen(@Param("citizenId") UUID citizenId);

  // Count fraud signals by citizen
  long countByCitizenId(UUID citizenId);

  // Count fraud signals by type
  long countByType(FraudType type);

  // Find citizens with multiple fraud signals
  @Query("SELECT f.citizen.id, COUNT(f) FROM FraudSignal f " +
         "GROUP BY f.citizen.id HAVING COUNT(f) >= :minCount")
  List<Object[]> findCitizensWithMultipleFraudSignals(@Param("minCount") long minCount);

  // Find recent fraud signals for a citizen
  List<FraudSignal> findByCitizenIdAndCreatedAtAfterOrderByCreatedAtDesc(
      UUID citizenId,
      LocalDateTime date
  );
}



