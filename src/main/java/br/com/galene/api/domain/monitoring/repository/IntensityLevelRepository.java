package br.com.galene.api.domain.monitoring.repository;

import br.com.galene.api.domain.monitoring.entity.IntensityLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntensityLevelRepository extends JpaRepository<IntensityLevelEntity, Long> {

    List<IntensityLevelEntity> findAllByOrderByDbMinAsc();
}
