package fr.perretpy.myBoxes.repository;

import fr.perretpy.myBoxes.model.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContainerRepository extends JpaRepository<Container, UUID> {
    Iterable<Container> findAllByLocationId(UUID id);
    Page<Container> findAllByLocationId(UUID id, Pageable pageable);
    Page<Container> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String queryName, String queryType, Pageable pageable);
}
