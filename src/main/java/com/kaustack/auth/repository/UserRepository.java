package com.kaustack.auth.repository;

import com.kaustack.auth.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.flags f WHERE f.id = :flagId")
    List<User> findAllByFlagId(@Param("flagId") UUID flagId);
}
