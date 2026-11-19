package br.com.galene.api.domain.monitoring.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementBatchRepository {

    void insertMeasurements(List<MeasurementRow> rows);

    void insertAlerts(Long patientId, List<LocalDateTime> measuredAt, LocalDateTime createdAt);

    Optional<Long> findAlertId(Long patientId, LocalDateTime measuredAt);

    int clearLocations(Long patientId);

    int deleteAllOfPatient(Long patientId);
}
