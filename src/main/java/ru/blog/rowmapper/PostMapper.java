package ru.blog.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.blog.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setLikesCount(rs.getInt("likes_count"));
        // comments_count вычисляется подзапросом в SQL
        post.setCommentsCount(rs.getInt("comments_count"));
        return post;
    }
}
