package ru.blog.dao;

import ru.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    // Получение постов с фильтрацией (поиск) и пагинацией
    List<Post> findAll(String titlePart, List<String> tags, int limit, int offset);

    // Подсчет общего количества постов
    int count(String titlePart, List<String> tags);

    // Получение одного поста
    Optional<Post> findById(Long id);

    // Создание поста
    Post create(Post post);

    // Обновление поста
    void update(Post post);

    // Удаление поста
    void delete(Long id);

    // Лайки
    void incrementLikes(Long postId);

    int getLikesCount(Long postId);

    // Работа с картинками
    void updateImage(Long postId, byte[] image);

    byte[] getImage(Long postId);

    // Вспомогательный метод для проверки существования
    boolean exists(Long id);
}
