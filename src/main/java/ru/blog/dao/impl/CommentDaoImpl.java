package ru.blog.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.blog.dao.CommentDao;
import ru.blog.model.Comment;
import ru.blog.rowmapper.CommentMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CommentDaoImpl implements CommentDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommentDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        String sql = "SELECT id, text, post_id FROM comments WHERE post_id = :postId ORDER BY id";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("postId", postId), new CommentMapper());
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT id, text, post_id FROM comments WHERE id = :id";
        try {
            Comment comment = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), new CommentMapper());
            return Optional.ofNullable(comment);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Comment create(Comment comment) {
        String sql = "INSERT INTO comments (text, post_id) VALUES (:text, :postId)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("text", comment.getText());
        params.addValue("postId", comment.getPostId());

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        comment.setId(keyHolder.getKey().longValue());

        return comment;
    }

    @Override
    public void update(Comment comment) {
        String sql = "UPDATE comments SET text = :text WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("text", comment.getText());
        params.addValue("id", comment.getId());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }
}
