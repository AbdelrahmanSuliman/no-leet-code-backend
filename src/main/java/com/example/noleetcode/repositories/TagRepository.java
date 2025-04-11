package com.example.noleetcode.repositories;

import com.example.noleetcode.enums.TagType;
import com.example.noleetcode.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagType(TagType tagType);

}
