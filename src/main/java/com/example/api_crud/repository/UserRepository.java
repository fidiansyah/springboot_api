package com.example.api_crud.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.api_crud.entity.UserEntity;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUuid(UUID uuid);
}
