package fr.perretpy.myBoxes.repository;

import fr.perretpy.myBoxes.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Page<Location> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String queryName, String queryDescription, Pageable pageable);
}
