package ru.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.blog.config.AppConfig;
import ru.blog.config.WebConfig;
import ru.blog.model.Comment;
import ru.blog.model.Post;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(classes = {AppConfig.class, WebConfig.class})
@Transactional
class ExtendedBlogTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    void shouldUpdatePost() throws Exception {
        Post post = createPost("Old Title", "Old Text", List.of("old"));

        post.setTitle("New Title");
        post.setText("New updated text content");
        post.setTags(List.of("new", "update"));

        mockMvc.perform(put("/api/posts/{id}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.tags[0]").value("new"));

        mockMvc.perform(get("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New updated text content"));
    }

    @Test
    void shouldDeletePost() throws Exception {
        Post post = createPost("To Delete", "Text", List.of());

        mockMvc.perform(delete("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}", post.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("not found")));
    }

    @Test
    void shouldIncrementLikes() throws Exception {
        Post post = createPost("Like Me", "Text", List.of());

        mockMvc.perform(post("/api/posts/{id}/likes", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("1")); // Возвращает int

        mockMvc.perform(post("/api/posts/{id}/likes", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));

        mockMvc.perform(get("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(2));
    }

    @Test
    void shouldManageCommentsLifecycle() throws Exception {
        Post post = createPost("Post for comments", "Text", List.of());

        Comment comment = new Comment();
        comment.setText("Original Comment");

        String response = mockMvc.perform(post("/api/posts/{id}/comments", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Comment createdComment = objectMapper.readValue(response, Comment.class);

        createdComment.setText("Updated Comment");
        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", post.getId(), createdComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated Comment"));

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), createdComment.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}/comments", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn404ForNonExistentPost() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", 99999L))
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.error").exists());
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