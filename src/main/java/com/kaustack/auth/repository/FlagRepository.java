package com.kaustack.auth.repository;

import com.kaustack.auth.model.Flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {
    boolean existsByName(String name);
}
