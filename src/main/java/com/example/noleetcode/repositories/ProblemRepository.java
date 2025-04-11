package com.example.noleetcode.repositories;

import com.example.noleetcode.models.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsByTitle(String title);

    Optional<Problem> findByUuid(UUID uuid);
}
