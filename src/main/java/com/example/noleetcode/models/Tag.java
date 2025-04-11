package com.example.noleetcode.models;

import com.example.noleetcode.enums.TagType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagType tagType;

    @ManyToMany(mappedBy = "tags")
    private List<Problem> problems;

    public Tag() {}

    /**
     * Constructor for creating a Tag with only its type.
     * Used for initializing tags from the enum.
     * @param tagType The type of the tag.
     */
    public Tag(TagType tagType) {
        this.tagType = tagType;
    }
    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
}
