package fr.perretpy.myBoxes.repository;

import fr.perretpy.myBoxes.model.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ContainerRepository extends JpaRepository<Container, UUID> {
    Iterable<Container> findAllByParentId(UUID id);
    Page<Container> findAllByParent_Id(UUID id, Pageable pageable);
    Page<Container> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String queryName, String queryType, Pageable pageable);

    @Query(value = "SELECT * FROM container c "+
                    "WHERE SIMILARITY(c.name, :query) > 0.3 "+
                    "ORDER BY SIMILARITY(c.name, :query) DESC",
            nativeQuery = true)
    Page<Container> fuzzySearchName(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM container c "+
            "WHERE SIMILARITY(c.description, :query) > 0.3 "+
            "ORDER BY SIMILARITY(c.description, :query) DESC",
            nativeQuery = true)
    Page<Container> fuzzySearchDescription(@Param("query") String query, Pageable pageable);
}
