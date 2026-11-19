package br.com.galene.api.domain.monitoring.repository;

import br.com.galene.api.domain.monitoring.entity.MeasurementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<MeasurementEntity, Long>, MeasurementBatchRepository {

    @Query("SELECT m.measuredAt FROM MeasurementEntity m WHERE m.patient.id = :patientId AND m.measuredAt BETWEEN :from AND :to")
    List<LocalDateTime> findMeasuredAtBetween(@Param("patientId") Long patientId, @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    @EntityGraph(attributePaths = "level")
    List<MeasurementEntity> findByPatientIdOrderByMeasuredAtAsc(Long patientId);

    @Query("""
            SELECT new br.com.galene.api.domain.monitoring.repository.ReadingPoint(m.dbValue, m.measuredAt, m.locationType, m.level.id)
            FROM MeasurementEntity m
            WHERE m.patient.id = :patientId AND m.measuredAt >= :start AND m.measuredAt < :end
            ORDER BY m.measuredAt""")
    List<ReadingPoint> findReadingPoints(@Param("patientId") Long patientId, @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    boolean existsByPatientIdAndMeasuredAtGreaterThanEqualAndMeasuredAtLessThan(Long patientId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT MIN(m.measuredAt) FROM MeasurementEntity m WHERE m.patient.id = :patientId")
    Optional<LocalDateTime> findFirstReadingTime(@Param("patientId") Long patientId);

    @Query("SELECT DISTINCT m.patient.id FROM MeasurementEntity m WHERE m.measuredAt >= :start AND m.measuredAt < :end")
    List<Long> findPatientsWithReadingsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @EntityGraph(attributePaths = "level")
    Page<MeasurementEntity> findByPatientIdAndMeasuredAtBetween(Long patientId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
