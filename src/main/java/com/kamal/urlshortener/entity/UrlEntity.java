package com.kamal.urlshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "urls")
@Getter
@Setter
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(unique = true, nullable = false, length = 6)
    private String shortCode;

    private LocalDateTime expiresAt;

    private Long clickCount;
    private String status;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.clickCount = 0L;
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
    }
}
