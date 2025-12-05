package ru.blog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blog.dao.PostDao;
import ru.blog.dto.PostListDto;
import ru.blog.model.Post;
import ru.blog.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private record SearchRequest(String titlePart, List<String> tags) {
    }

    private final PostDao postDao;

    public PostServiceImpl(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    @Transactional(readOnly = true)
    public PostListDto getPosts(String search, int pageNumber, int pageSize) {
        SearchRequest searchRequest = parseSearchString(search);

        int offset = (pageNumber - 1) * pageSize;
        int totalPosts = postDao.count(searchRequest.titlePart, searchRequest.tags);
        int lastPage = (int) Math.ceil((double) totalPosts / pageSize);
        if (lastPage == 0) lastPage = 1;

        List<Post> posts = postDao.findAll(searchRequest.titlePart, searchRequest.tags, pageSize, offset);

        List<Post> previewPosts = posts.stream().map(this::truncateText).collect(Collectors.toList());

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        return new PostListDto(previewPosts, hasPrev, hasNext, lastPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return postDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Override
    @Transactional
    public Post createPost(Post post) {
        return postDao.create(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, Post post) {
        if (!postDao.exists(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        post.setId(id);
        postDao.update(post);

        return getPost(id);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        if (!postDao.exists(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postDao.delete(id);
    }

    @Override
    @Transactional
    public int likePost(Long id) {
        if (!postDao.exists(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postDao.incrementLikes(id);
        return postDao.getLikesCount(id);
    }

    @Override
    @Transactional
    public void updatePostImage(Long id, byte[] imageBytes) {
        if (!postDao.exists(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postDao.updateImage(id, imageBytes);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getPostImage(Long id) {
        return postDao.getImage(id);
    }


    private Post truncateText(Post original) {
        String text = original.getText();
        if (text != null && text.length() > 128) {
            original.setText(text.substring(0, 128) + "...");
        }
        return original;
    }

    private SearchRequest parseSearchString(String search) {
        if (search == null || search.isBlank()) {
            return new SearchRequest(null, null);
        }

        String[] words = search.trim().split("\\s+");
        List<String> tags = new ArrayList<>();
        List<String> titleWords = new ArrayList<>();

        for (String word : words) {
            if (word.isBlank()) continue;

            if (word.startsWith("#") && word.length() > 1) {
                tags.add(word.substring(1));
            } else {
                titleWords.add(word);
            }
        }

        String titlePart = String.join(" ", titleWords);
        if (titlePart.isBlank()) titlePart = null;
        if (tags.isEmpty()) tags = null;

        return new SearchRequest(titlePart, tags);
    }


}
