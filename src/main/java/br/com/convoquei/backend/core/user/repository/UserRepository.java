package br.com.convoquei.backend.core.user.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.core.user.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}