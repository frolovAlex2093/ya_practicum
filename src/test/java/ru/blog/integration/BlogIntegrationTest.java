package ru.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.blog.model.Comment;
import ru.blog.model.Post;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndGetPost() throws Exception {
        Post newPost = new Post();
        newPost.setTitle("Integration Test Post");
        newPost.setText("Some interesting text about testing.");
        newPost.setTags(List.of("test", "junit"));

        String postJson = objectMapper.writeValueAsString(newPost);

        String responseContent = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Post"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andReturn().getResponse().getContentAsString();

        Post createdPost = objectMapper.readValue(responseContent, Post.class);
        Long id = createdPost.getId();

        mockMvc.perform(get("/api/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Some interesting text about testing."));
    }

    @Test
    void shouldSearchPostsByTagAndTitle() throws Exception {
        createPost("Java Basics", "About Java", List.of("java"));
        createPost("Spring Boot", "About Spring", List.of("java", "spring"));
        createPost("Python", "Snake language", List.of("python"));

        mockMvc.perform(get("/api/posts")
                        .param("search", "#java")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(2)));

        mockMvc.perform(get("/api/posts")
                        .param("search", "Boot")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].title").value("Spring Boot"));
    }

    @Test
    void shouldAddAndGetComments() throws Exception {
        Post post = createPost("Post for comments", "Text", List.of());
        Long postId = post.getId();

        Comment comment = new Comment();
        comment.setText("First comment!");

        mockMvc.perform(post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("First comment!"));

        mockMvc.perform(get("/api/posts/{id}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("First comment!"));
    }

    @Test
    void shouldUploadImage() throws Exception {
        Post post = createPost("Image Post", "Text", List.of());

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3, 4}
        );

        mockMvc.perform(multipart("/api/posts/{id}/image", post.getId())
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        // Проверяем загрузку
        mockMvc.perform(get("/api/posts/{id}/image", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[]{1, 2, 3, 4}));
    }

    private Post createPost(String title, String text, List<String> tags) throws Exception {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setTags(tags);

        String content = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(content, Post.class);
    }
}