package com.example.noleetcode.repositories;

import com.example.noleetcode.models.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
