package fr.perretpy.myBoxes.repository;

import fr.perretpy.myBoxes.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Iterable<Item> findAllByContainerId(UUID id);

    Page<Item> findAllByContainerId(UUID id, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String queryName, String queryDescription, Pageable pageable);

    @Query(value = "SELECT * FROM item i "+
            "WHERE SIMILARITY(i.name, :query) > 0.3 "+
            "ORDER BY SIMILARITY(i.name, :query) DESC",
            nativeQuery = true)
    Page<Item> fuzzySearchName(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM item i "+
            "WHERE SIMILARITY(i.description, :query) > 0.3 "+
            "ORDER BY SIMILARITY(i.description, :query) DESC",
            nativeQuery = true)
    Page<Item> fuzzySearchDescription(@Param("query") String query, Pageable pageable);
}
