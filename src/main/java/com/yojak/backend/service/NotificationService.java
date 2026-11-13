package com.yojak.backend.service;

import com.yojak.backend.dto.NotificationResponse;
import com.yojak.backend.entity.*;
import com.yojak.backend.repository.NotificationRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    @Transactional
    public void notifyLike(Post post, User actor) {
        User recipient = post.getUser();
        if (isSameUser(recipient, actor)) return;
        if (notificationRepository.existsByRecipientIdAndActorIdAndTypeAndPostSlug(
                recipient.getId(), actor.getId(), NotificationType.LIKE, post.getSlug())) return;

        save(recipient, actor, NotificationType.LIKE, post, null);
    }

    @Transactional
    public void removeLike(String postSlug, User actor) {
        notificationRepository.deleteByActorAndTypeAndPost(actor.getId(), NotificationType.LIKE, postSlug);
    }

    @Transactional
    public void notifyComment(Post post, User actor, Comment comment) {
        User recipient = post.getUser();
        if (isSameUser(recipient, actor)) return;
        save(recipient, actor, NotificationType.COMMENT, post, comment.getId());
    }

    @Transactional
    public void notifyReply(Comment parent, User actor, Comment reply) {
        User recipient = parent.getUser();
        if (isSameUser(recipient, actor)) return;
        save(recipient, actor, NotificationType.REPLY, parent.getPost(), reply.getId());
    }

    @Transactional
    public void notifyFollow(User target, User actor) {
        if (isSameUser(target, actor)) return;
        if (notificationRepository.existsByRecipientIdAndActorIdAndType(
                target.getId(), actor.getId(), NotificationType.FOLLOW)) return;

        save(target, actor, NotificationType.FOLLOW, null, null);
    }

    @Transactional
    public void removeFollow(User target, User actor) {
        notificationRepository.deleteByRecipientAndActorAndType(
                target.getId(), actor.getId(), NotificationType.FOLLOW);
    }


    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String userEmail) {
        User user = getUser(userEmail);
        return notificationRepository.findTop50ByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getUnreadCount(String userEmail) {
        User user = getUser(userEmail);
        return Map.of("count", notificationRepository.countByRecipientIdAndSeenFalse(user.getId()));
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = getUser(userEmail);
        notificationRepository.markAllAsRead(user.getId());
    }

    @Transactional
    public void markAsRead(UUID notificationId, String userEmail) {
        User user = getUser(userEmail);
        notificationRepository.findByIdAndRecipientId(notificationId, user.getId())
                .ifPresent(n -> n.setSeen(true));
    }


    private void save(User recipient, User actor, NotificationType type, Post post, UUID commentId) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setActor(actor);
        n.setType(type);
        n.setPost(post);
        n.setCommentId(commentId);
        n.setSeen(false);
        notificationRepository.save(n);
    }

    private boolean isSameUser(User a, User b) {
        return a.getId().equals(b.getId());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private NotificationResponse toResponse(Notification n) {
        User actor = n.getActor();
        Post post = n.getPost();
        return new NotificationResponse(
                n.getId(),
                n.getType().name(),
                actor.getId(),
                actor.getName(),
                actor.getAvatar(),
                post != null ? post.getSlug() : null,
                post != null ? post.getTitle() : null,
                n.getCommentId(),
                n.isSeen(),
                n.getCreatedAt()
        );
    }
}