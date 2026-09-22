package com.yojak.backend.repository;

import com.yojak.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    Optional<Post> findBySlug(String slug);

    List<Post> findByStatus(String status);

    List<Post> findByUserId(UUID userId);

    boolean existsBySlug(String slug);
}