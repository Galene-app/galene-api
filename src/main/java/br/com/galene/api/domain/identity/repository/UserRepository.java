package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.UserEntity;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByIdAndAccountStatusNot(Long id, AccountStatus accountStatus);

    Optional<UserEntity> findByEmailAndAccountStatusNot(String email, AccountStatus accountStatus);
}
