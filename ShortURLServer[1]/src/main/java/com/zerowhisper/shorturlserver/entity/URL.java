package com.zerowhisper.shorturlserver.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(indexes = {
        @Index(name = "index_original_url", columnList = "original_url"),
        @Index(name = "index_shortened_url", columnList = "shortened_url")
})
public class URL {
    @Id
    private Long id; // Snowflake ID

    @Column(name = "original_url", unique = true, nullable = false)
    private String originalURL;

    @Column(name = "shortened_url", unique = true, nullable = false)
    private String shortenedURL;
}
