package com.example.noleetcode.repositories;

import com.example.noleetcode.models.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}
