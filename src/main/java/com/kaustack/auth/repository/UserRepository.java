package com.kaustack.auth.repository;

import com.kaustack.auth.model.Gender;
import com.kaustack.auth.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findAllByFlagsId(UUID flagId);

    @Query("select u.gender as gender, count(u) as accountCount from User u group by u.gender")
    List<GenderCount> countAccountsByGender();

    interface GenderCount {
        Gender getGender();

        long getAccountCount();
    }
}
