package com.yojak.backend.repository;

import com.yojak.backend.entity.Notification;
import com.yojak.backend.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findTop50ByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    long countByRecipientIdAndSeenFalse(UUID recipientId);

    Optional<Notification> findByIdAndRecipientId(UUID id, UUID recipientId);

    boolean existsByRecipientIdAndActorIdAndTypeAndPostSlug(UUID recipientId, UUID actorId,
                                                            NotificationType type, String postSlug);

    boolean existsByRecipientIdAndActorIdAndType(UUID recipientId, UUID actorId, NotificationType type);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = true WHERE n.recipient.id = :recipientId AND n.seen = false")
    int markAllAsRead(@Param("recipientId") UUID recipientId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.actor.id = :actorId AND n.type = :type AND n.post.slug = :postSlug")
    void deleteByActorAndTypeAndPost(@Param("actorId") UUID actorId,
                                     @Param("type") NotificationType type,
                                     @Param("postSlug") String postSlug);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipient.id = :recipientId AND n.actor.id = :actorId AND n.type = :type")
    void deleteByRecipientAndActorAndType(@Param("recipientId") UUID recipientId,
                                          @Param("actorId") UUID actorId,
                                          @Param("type") NotificationType type);
}