package ru.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private List<String> tags = new ArrayList<>();
    private Integer likesCount = 0;
    private Integer commentsCount = 0;
    private byte[] image;
}
