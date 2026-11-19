package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByUserId(Long userId);

    @Query(value = "SELECT NVL(MAX(TO_NUMBER(SUBSTR(patient_code, 5))), 0) FROM gln_patient", nativeQuery = true)
    Long findLastPatientCodeNumber();
}
