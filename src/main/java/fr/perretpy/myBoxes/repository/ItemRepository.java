package fr.perretpy.myBoxes.repository;

import fr.perretpy.myBoxes.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Iterable<Item> findAllByContainerId(UUID id);

    Page<Item> findAllByContainerId(UUID id, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String queryName, String queryDescription, Pageable pageable);
}
