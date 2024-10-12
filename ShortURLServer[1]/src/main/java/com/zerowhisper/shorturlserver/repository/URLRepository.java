package com.zerowhisper.shorturlserver.repository;

import com.zerowhisper.shorturlserver.entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<URL, Long> {
    @Query("SELECT u FROM URL u WHERE u.originalURL = ?1")
    Optional<URL> findByOriginalURL(String originalURL);

    @Query("SELECT u FROM URL u WHERE u.shortenedURL = ?1")
    Optional<URL> findByShortenedURL(String shortenedURL);
}
