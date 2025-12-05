package ru.blog.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.blog.dto.PostListDto;
import ru.blog.model.Post;
import ru.blog.service.PostService;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PostListDto getPosts(
            @RequestParam(required = false) String search,
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        return postService.getPosts(search, pageNumber, pageSize);
    }


    @PostMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @PostMapping("/{id}/likes")
    public int likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }

    @PutMapping("/{id}/image")
    public void uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        try {
            postService.updatePostImage(id, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error reading image file", e);
        }
    }

    @GetMapping(value = "/{id}/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] getImage(@PathVariable Long id) {
        return postService.getPostImage(id);
    }
}
