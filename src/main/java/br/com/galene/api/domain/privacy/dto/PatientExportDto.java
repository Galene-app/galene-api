package br.com.galene.api.domain.privacy.dto;

import br.com.galene.api.domain.identity.entity.PatientEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PatientExportDto(
        String patientCode,
        LocalDate startDate,
        Boolean hasMedicalCare,
        String declaredCondition,
        BigDecimal dbLimit
) {

    public PatientExportDto(PatientEntity patient) {
        this(patient.getPatientCode(), patient.getStartDate(), patient.getHasMedicalCare(), patient.getDiagnosis(), patient.getDbLimit());
    }
}
