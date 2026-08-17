package com.kaustack.auth.repository;

import com.kaustack.auth.model.Flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {
    Optional<Flag> findByName(String name);

    boolean existsByName(String name);
}
