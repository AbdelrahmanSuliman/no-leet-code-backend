package com.example.noleetcode.repositories;

import com.example.noleetcode.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}
