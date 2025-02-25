package fr.perretpy.MyBoxes.repository;

import fr.perretpy.MyBoxes.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
