package ru.blog.controller;

import org.springframework.web.bind.annotation.*;
import ru.blog.model.Comment;
import ru.blog.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    @PostMapping
    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        return commentService.addComment(postId, comment);
    }

    @GetMapping("/{commentId}")
    public Comment getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return commentService.getComment(postId, commentId);
    }

    @PutMapping("/{commentId}")
    public Comment updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody Comment comment
    ) {
        return commentService.updateComment(postId, commentId, comment);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(postId, commentId);
    }
}
