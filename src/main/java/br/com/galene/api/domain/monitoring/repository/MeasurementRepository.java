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

public interface MeasurementRepository extends JpaRepository<MeasurementEntity, Long>, MeasurementBatchRepository {

    @Query("SELECT m.measuredAt FROM MeasurementEntity m WHERE m.patient.id = :patientId AND m.measuredAt BETWEEN :from AND :to")
    List<LocalDateTime> findMeasuredAtBetween(@Param("patientId") Long patientId, @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    @EntityGraph(attributePaths = "level")
    Page<MeasurementEntity> findByPatientIdAndMeasuredAtBetween(Long patientId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
