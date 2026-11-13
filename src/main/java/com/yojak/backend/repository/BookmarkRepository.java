package com.yojak.backend.repository;

import com.yojak.backend.entity.Bookmark;
import com.yojak.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    boolean existsByPostSlugAndUserId(String postSlug, UUID userId);

    Optional<Bookmark> findByPostSlugAndUserId(String postSlug, UUID userId);

    @Query("SELECT b.post FROM Bookmark b WHERE b.user.id = :userId " +
            "AND (b.post.status = 'active' OR b.post.user.id = :userId) " +
            "ORDER BY b.createdAt DESC")
    List<Post> findSavedPosts(@Param("userId") UUID userId);
}