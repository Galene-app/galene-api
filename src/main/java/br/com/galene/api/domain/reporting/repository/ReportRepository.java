package br.com.galene.api.domain.reporting.repository;

import br.com.galene.api.domain.reporting.entity.ReportEntity;
import br.com.galene.api.domain.reporting.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    boolean existsByPatientIdAndReportTypeAndPeriodStart(Long patientId, ReportType reportType, LocalDate periodStart);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM ReportEntity r WHERE r.patientId = :patientId")
    int deleteAllOfPatient(@Param("patientId") Long patientId);
}
