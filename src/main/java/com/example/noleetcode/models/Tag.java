package com.example.noleetcode.models;

import com.example.noleetcode.enums.TagType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TagType tagType;

    @ManyToMany(mappedBy = "tags")
    private List<Problem> problems;

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }
}
