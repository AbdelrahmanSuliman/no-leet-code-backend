package com.example.noleetcode.repositories;

import com.example.noleetcode.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
