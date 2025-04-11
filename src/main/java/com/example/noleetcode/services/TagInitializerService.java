package com.example.noleetcode.services;

import com.example.noleetcode.enums.TagType; // Import TagType enum
import com.example.noleetcode.models.Tag; // Import Tag model
import com.example.noleetcode.repositories.TagRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // Import Transactional

import java.util.Arrays;

@Service
public class TagInitializerService {

    // Add logger
    private static final Logger logger = LoggerFactory.getLogger(TagInitializerService.class);

    private final TagRepository tagRepository;

    public TagInitializerService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @PostConstruct // This method runs after the bean is initialized
    @Transactional // Ensures the operation is performed within a transaction
    public void initializeTags() {
        logger.info("Initializing tags from TagType enum...");

        // Iterate through all values in the TagType enum
        for (TagType tagType : TagType.values()) {
            // Check if a tag with this type already exists
            // Assumes findByTagType method exists in TagRepository
            if (tagRepository.findByTagType(tagType).isEmpty()) {
                // If it doesn't exist, create and save a new Tag
                Tag newTag = new Tag(tagType); // Assumes Tag constructor Tag(TagType) exists
                tagRepository.save(newTag);
                logger.info("Created and saved tag: {}", tagType);
            } else {
                logger.debug("Tag already exists for type: {}", tagType);
            }
        }
        logger.info("Tag initialization complete.");
    }
}