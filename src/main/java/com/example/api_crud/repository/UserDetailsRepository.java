package com.example.api_crud.repository;

import com.example.api_crud.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, Long> {
    Optional<UserDetailsEntity> findByUser_Uuid(UUID userUuid);
}