package ru.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.blog.model.Post;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListDto {
    private List<Post> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;
}
