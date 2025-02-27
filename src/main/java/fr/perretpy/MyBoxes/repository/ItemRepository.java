package fr.perretpy.MyBoxes.repository;

import fr.perretpy.MyBoxes.model.Container;
import fr.perretpy.MyBoxes.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Iterable<Item> findAllByContainerId(UUID id);

    Page<Item> findAllByContainerId(UUID id, Pageable pageable);
}
