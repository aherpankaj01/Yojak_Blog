package com.yojak.backend.repository;

import com.yojak.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    long countByPostSlug(String postSlug);

    Optional<Like> findByPostSlugAndUserId(String postSlug, UUID userId);

    boolean existsByPostSlugAndUserId(String postSlug, UUID userId);
}