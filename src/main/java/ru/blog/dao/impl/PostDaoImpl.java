package ru.blog.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.blog.dao.PostDao;
import ru.blog.model.Post;
import ru.blog.rowmapper.PostMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class PostDaoImpl implements PostDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll(String titlePart, List<String> tags, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.title, p.text, p.likes_count, " +
                        "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as comments_count " +
                        "FROM posts p "
        );

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        // Блок условий WHERE
        addFilteringCondition(sql, params, titlePart, tags);

        sql.append(" ORDER BY p.id DESC LIMIT :limit OFFSET :offset");

        List<Post> posts = jdbcTemplate.query(sql.toString(), params, new PostMapper());

        posts.forEach(this::loadTagsForPost);

        return posts;
    }

    @Override
    public int count(String titlePart, List<String> tags) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM posts p ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        addFilteringCondition(sql, params, titlePart, tags);

        Integer count = jdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT p.id, p.title, p.text, p.likes_count, " +
                "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as comments_count " +
                "FROM posts p WHERE p.id = :id";

        try {
            Post post = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), new PostMapper());
            if (post != null) {
                loadTagsForPost(post);
            }
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Post create(Post post) {
        String sql = "INSERT INTO posts (title, text, likes_count) VALUES (:title, :text, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", post.getTitle());
        params.addValue("text", post.getText());

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        Long newId = keyHolder.getKey().longValue();
        post.setId(newId);

        // Сохраняем теги
        saveTags(newId, post.getTags());

        return post;
    }

    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET title = :title, text = :text WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", post.getTitle());
        params.addValue("text", post.getText());
        params.addValue("id", post.getId());

        jdbcTemplate.update(sql, params);

        jdbcTemplate.update("DELETE FROM post_tags WHERE post_id = :id", params);
        saveTags(post.getId(), post.getTags());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public void incrementLikes(Long postId) {
        String sql = "UPDATE posts SET likes_count = likes_count + 1 WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource("id", postId));
    }

    @Override
    public int getLikesCount(Long postId) {
        String sql = "SELECT likes_count FROM posts WHERE id = :id";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", postId), Integer.class);
            return count != null ? count : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public void updateImage(Long postId, byte[] image) {
        String sql = "UPDATE posts SET image_data = :image WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("image", image);
        params.addValue("id", postId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public byte[] getImage(Long postId) {
        String sql = "SELECT image_data FROM posts WHERE id = :id";
        try {
            return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", postId), byte[].class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean exists(Long id) {
        String sql = "SELECT COUNT(*) FROM posts WHERE id = :id";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), Integer.class);
        return count != null && count > 0;
    }

    // Хелпер для формирования WHERE
    private void addFilteringCondition(StringBuilder sql, MapSqlParameterSource params, String titlePart, List<String> tags) {
        boolean whereAdded = false;

        // Фильтр по названию
        if (titlePart != null && !titlePart.isBlank()) {
            sql.append(" WHERE LOWER(p.title) LIKE :titlePart");
            params.addValue("titlePart", "%" + titlePart.toLowerCase() + "%");
            whereAdded = true;
        }

        // Фильтр по тегам
        if (tags != null && !tags.isEmpty()) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
            }
            // Выбираем шв постов, у которых есть перечисленные теги
            sql.append("p.id IN (SELECT pt.post_id FROM post_tags pt WHERE pt.tag IN (:tags) " +
                    "GROUP BY pt.post_id HAVING COUNT(DISTINCT pt.tag) = :tagsCount)");

            params.addValue("tags", tags);
            params.addValue("tagsCount", tags.size());
        }
    }

    private void loadTagsForPost(Post post) {
        String sql = "SELECT tag FROM post_tags WHERE post_id = :postId";
        List<String> tags = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("postId", post.getId()),
                (rs, rowNum) -> rs.getString("tag")
        );
        post.setTags(tags);
    }

    private void saveTags(Long postId, List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        String sql = "INSERT INTO post_tags (post_id, tag) VALUES (:postId, :tag)";
        MapSqlParameterSource[] batchParams = tags.stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("postId", postId)
                        .addValue("tag", tag))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

}
