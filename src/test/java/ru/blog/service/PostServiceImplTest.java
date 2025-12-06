package ru.blog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.blog.dao.PostDao;
import ru.blog.dto.PostListDto;
import ru.blog.model.Post;
import ru.blog.service.impl.PostServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostDao postDao;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getPosts_shouldParseSearchStringCorrectly() {
        String search = "#java Lesson #spring";
        int pageNumber = 1;
        int pageSize = 10;

        when(postDao.count(any(), any())).thenReturn(5);
        when(postDao.findAll(any(), any(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        postService.getPosts(search, pageNumber, pageSize);

        verify(postDao).findAll(eq("Lesson"), eq(List.of("java", "spring")), eq(pageSize), eq(0));
    }

    @Test
    void getPosts_shouldTruncateLongText() {
        String longText = "a".repeat(200); // 200 символов
        Post post = new Post(1L, "Title", longText, List.of(), 0, 0, null);

        when(postDao.count(any(), any())).thenReturn(1);
        when(postDao.findAll(any(), any(), anyInt(), anyInt())).thenReturn(List.of(post));

        PostListDto result = postService.getPosts(null, 1, 10);

        assertThat(result.getPosts()).hasSize(1);
        String previewText = result.getPosts().get(0).getText();
        assertThat(previewText).hasSize(131); // 128 chars + "..."
        assertThat(previewText).endsWith("...");
    }

    @Test
    void getPosts_shouldCalculatePaginationCorrectly() {
        when(postDao.count(null, null)).thenReturn(25);
        when(postDao.findAll(null, null, 10, 10)).thenReturn(Collections.emptyList());

        PostListDto result = postService.getPosts(null, 2, 10);

        assertThat(result.isHasPrev()).isTrue(); // есть 1 страница
        assertThat(result.isHasNext()).isTrue(); // есть 3 страница
        assertThat(result.getLastPage()).isEqualTo(3); // 25 / 10 = 2.5 -> 3 страницы
    }

    @Test
    void getPost_shouldThrowException_whenNotFound() {
        when(postDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.getPost(99L));
    }
}