package ru.blog.service;

import ru.blog.dto.PostListDto;
import ru.blog.model.Post;

public interface PostService {
    PostListDto getPosts(String search, int pageNumber, int pageSize);

    Post getPost(Long id);

    Post createPost(Post post);

    Post updatePost(Long id, Post post);

    void deletePost(Long id);

    int likePost(Long id);

    void updatePostImage(Long id, byte[] imageBytes);

    byte[] getPostImage(Long id);
}
