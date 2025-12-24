package ru.blog.dao;

import ru.blog.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    // Получение коментов
    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findById(Long id);

    // Создание коментов
    Comment create(Comment comment);

    // Обновление коментов
    void update(Comment comment);

    // Удаление коментов
    void delete(Long id);
}
