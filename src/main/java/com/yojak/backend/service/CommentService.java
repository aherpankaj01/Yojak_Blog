package com.yojak.backend.service;

import com.yojak.backend.dto.CommentRequest;
import com.yojak.backend.dto.CommentResponse;
import com.yojak.backend.entity.Comment;
import com.yojak.backend.entity.Post;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.CommentRepository;
import com.yojak.backend.repository.PostRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CommentResponse addComment(String postSlug, CommentRequest request, String userEmail) {
        Post post = postRepository.findBySlug(postSlug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

            if (!parent.getPost().getSlug().equals(postSlug)) {
                throw new IllegalArgumentException("Parent comment does not belong to this post");
            }
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(user);
        comment.setParent(parent);

        Comment saved = commentRepository.save(comment);

        if (parent != null) {
            notificationService.notifyReply(parent, user, saved);
        }

        boolean ownerAlreadyNotified = parent != null
                && parent.getUser().getId().equals(post.getUser().getId());
        if (!ownerAlreadyNotified) {
            notificationService.notifyComment(post, user, saved);
        }

        return toResponse(saved);
    }

    public List<CommentResponse> getComments(String postSlug) {
        return commentRepository.findByPostSlugOrderByCreatedAtDesc(postSlug)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteComment(UUID commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getCreatedAt(),
                comment.getParent() != null ? comment.getParent().getId() : null
        );
    }
}