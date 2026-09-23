package com.yojak.backend.repository;

import com.yojak.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    Optional<Post> findBySlug(String slug);

    List<Post> findByStatus(String status);

    List<Post> findByStatusOrderByCreatedAtDesc(String status);

    List<Post> findByUserId(UUID userId);

    boolean existsBySlug(String slug);

    @Query("SELECT p FROM Post p LEFT JOIN Like l ON l.post = p WHERE p.status = :status GROUP BY p ORDER BY COUNT(l) DESC, p.createdAt DESC")
    List<Post> findByStatusOrderByLikeCountDesc(@Param("status") String status);

    @Query("SELECT p FROM Post p LEFT JOIN Comment c ON c.post = p WHERE p.status = :status GROUP BY p ORDER BY COUNT(c) DESC, p.createdAt DESC")
    List<Post> findByStatusOrderByCommentCountDesc(@Param("status") String status);

    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT p FROM Post p LEFT JOIN Like l ON l.post = p WHERE p.user.id = :userId GROUP BY p ORDER BY COUNT(l) DESC, p.createdAt DESC")
    List<Post> findByUserIdOrderByLikeCountDesc(@Param("userId") UUID userId);

    @Query("SELECT p FROM Post p LEFT JOIN Comment c ON c.post = p WHERE p.user.id = :userId GROUP BY p ORDER BY COUNT(c) DESC, p.createdAt DESC")
    List<Post> findByUserIdOrderByCommentCountDesc(@Param("userId") UUID userId);
}