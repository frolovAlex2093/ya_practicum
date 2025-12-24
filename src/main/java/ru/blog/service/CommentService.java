package ru.blog.service;

import ru.blog.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getComments(Long postId);

    Comment getComment(Long postId, Long commentId);

    Comment addComment(Long postId, Comment comment);

    Comment updateComment(Long postId, Long commentId, Comment comment);

    void deleteComment(Long postId, Long commentId);
}
