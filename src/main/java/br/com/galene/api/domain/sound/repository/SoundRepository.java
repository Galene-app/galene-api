package br.com.galene.api.domain.sound.repository;

import br.com.galene.api.domain.sound.entity.SoundEntity;
import br.com.galene.api.domain.sound.enums.SoundType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SoundRepository extends JpaRepository<SoundEntity, Long> {

    List<SoundEntity> findAllByOrderByIdAsc();

    List<SoundEntity> findBySoundTypeOrderByIdAsc(SoundType soundType);

    List<SoundEntity> findBySoundTypeInOrderByIdAsc(Collection<SoundType> soundTypes);
}
