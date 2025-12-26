package ru.blog.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.blog.dto.PostListDto;
import ru.blog.model.Post;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Test
    void shouldCreateAndRetrievePostViaService() {
        Post newPost = new Post();
        newPost.setTitle("Service Layer Test");
        newPost.setText("Testing service directly with Spring Boot Context");
        newPost.setTags(List.of("spring", "service"));

        Post created = postService.createPost(newPost);

        assertThat(created.getId()).isNotNull();

        Post retrieved = postService.getPost(created.getId());

        assertThat(retrieved.getTitle()).isEqualTo("Service Layer Test");
        assertThat(retrieved.getTags()).contains("spring", "service");
    }

    @Test
    void shouldCalculatePaginationInService() {
        for (int i = 0; i < 5; i++) {
            postService.createPost(new Post(null, "Title " + i, "Text", null, 0, 0, null));
        }

        PostListDto result = postService.getPosts(null, 1, 2);

        assertThat(result.getPosts()).hasSize(2);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getLastPage()).isGreaterThanOrEqualTo(3);
    }
}