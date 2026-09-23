package com.yojak.backend.repository;

import com.yojak.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByPostSlugOrderByCreatedAtDesc(String postSlug);

    long countByPostSlug(String postSlug);
}