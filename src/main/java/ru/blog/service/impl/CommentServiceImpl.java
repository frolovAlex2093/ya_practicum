package ru.blog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blog.dao.CommentDao;
import ru.blog.dao.PostDao;
import ru.blog.model.Comment;
import ru.blog.service.CommentService;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final PostDao postDao;

    public CommentServiceImpl(CommentDao commentDao, PostDao postDao) {
        this.commentDao = commentDao;
        this.postDao = postDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getComments(Long postId) {
        if (!postDao.exists(postId)) {
            throw new RuntimeException("Post not found - id: " + postId);
        }
        return commentDao.findAllByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getComment(Long postId, Long commentId) {
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found - id: " + commentId));

        if (!comment.getPostId().equals(postId)) {
            throw new RuntimeException("Comment " + commentId + " does not belong to post " + postId);
        }
        return comment;
    }

    @Override
    @Transactional
    public Comment addComment(Long postId, Comment comment) {
        if (!postDao.exists(postId)) {
            throw new RuntimeException("Post not found - id: " + postId);
        }
        comment.setPostId(postId);
        return commentDao.create(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long postId, Long commentId, Comment comment) {
        Comment existing = getComment(postId, commentId);

        existing.setText(comment.getText());
        commentDao.update(existing);

        return existing;
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        getComment(postId, commentId);
        commentDao.delete(commentId);
    }
}
