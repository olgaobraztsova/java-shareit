package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(
            "SELECT comment " +
                    "FROM Comment AS comment " +
                    "WHERE comment.item IN ?1"
    )
    List<Comment> findCommentsForItemsIn(List<Item> items, Sort sort);

    List<Comment> findByItemId(Integer itemId);
}
