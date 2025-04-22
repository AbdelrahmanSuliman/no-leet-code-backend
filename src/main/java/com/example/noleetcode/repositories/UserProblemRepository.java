package com.example.noleetcode.repositories;

import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.User;
import com.example.noleetcode.models.UserProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {
        Optional<UserProblem> findByUserAndProblem(User user, Problem problem);
        List<UserProblem> findAllByUser(User user);
}
