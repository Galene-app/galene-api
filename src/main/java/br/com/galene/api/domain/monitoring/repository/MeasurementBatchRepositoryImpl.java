package br.com.galene.api.domain.monitoring.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MeasurementBatchRepositoryImpl implements MeasurementBatchRepository {

    private static final String INSERT_MEASUREMENT = """
            INSERT INTO gln_measurement (id_patient, id_level, location_type, db_value, measured_at, source)
            VALUES (?, ?, ?, ?, ?, ?)""";
    private static final String INSERT_ALERT = """
            INSERT INTO gln_alert (id_measurement, created_at)
            SELECT id_measurement, ? FROM gln_measurement WHERE id_patient = ? AND measured_at = ?""";
    private static final String FIND_ALERT = """
            SELECT a.id_alert FROM gln_alert a JOIN gln_measurement m ON m.id_measurement = a.id_measurement
            WHERE m.id_patient = ? AND m.measured_at = ?""";
    private static final String CLEAR_LOCATIONS = """
            UPDATE gln_measurement SET location_type = NULL, id_location = NULL
            WHERE id_patient = ? AND (location_type IS NOT NULL OR id_location IS NOT NULL)""";
    private static final String DELETE_ALERTS = """
            DELETE FROM gln_alert WHERE id_measurement IN (SELECT id_measurement FROM gln_measurement WHERE id_patient = ?)""";
    private static final String DELETE_MEASUREMENTS = "DELETE FROM gln_measurement WHERE id_patient = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertMeasurements(List<MeasurementRow> rows) {
        jdbcTemplate.batchUpdate(INSERT_MEASUREMENT, rows, rows.size(), (statement, row) -> {
            statement.setLong(1, row.patientId());
            statement.setLong(2, row.levelId());
            statement.setString(3, row.locationType() == null ? null : row.locationType().name());
            statement.setBigDecimal(4, row.dbValue());
            statement.setTimestamp(5, Timestamp.valueOf(row.measuredAt()));
            statement.setString(6, row.source().name());
        });
    }

    @Override
    public void insertAlerts(Long patientId, List<LocalDateTime> measuredAt, LocalDateTime createdAt) {
        jdbcTemplate.batchUpdate(INSERT_ALERT, measuredAt, measuredAt.size(), (statement, readingTime) -> {
            statement.setTimestamp(1, Timestamp.valueOf(createdAt));
            statement.setLong(2, patientId);
            statement.setTimestamp(3, Timestamp.valueOf(readingTime));
        });
    }

    @Override
    public Optional<Long> findAlertId(Long patientId, LocalDateTime measuredAt) {
        return jdbcTemplate.queryForList(FIND_ALERT, Long.class, patientId, Timestamp.valueOf(measuredAt)).stream().findFirst();
    }

    @Override
    public int clearLocations(Long patientId) {
        return jdbcTemplate.update(CLEAR_LOCATIONS, patientId);
    }

    @Override
    public int deleteAllOfPatient(Long patientId) {
        jdbcTemplate.update(DELETE_ALERTS, patientId);
        return jdbcTemplate.update(DELETE_MEASUREMENTS, patientId);
    }
}
