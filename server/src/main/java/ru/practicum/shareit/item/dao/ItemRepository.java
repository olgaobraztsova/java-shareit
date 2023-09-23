package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerIdOrderById(Integer ownerId, Pageable page);

    @Query("select i from Item i " +
            "where lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%')) " +
            "and i.available = true ")
    List<Item> findBySearchKey(String searchKey);

    List<Item> findAllByRequestIdIn(List<Integer> itemRequests);
}
