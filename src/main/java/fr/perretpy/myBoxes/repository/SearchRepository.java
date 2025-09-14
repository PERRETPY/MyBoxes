package fr.perretpy.myBoxes.repository;

import fr.perretpy.myBoxes.dto.SearchResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<SearchResponseDto, Long> {

    @Query(value = """

            SELECT id, type, name, description, score
        FROM (
            SELECT c.id AS id,
                   'CONTAINER' AS type,
                   c.name AS name,
                   c.description AS description,
                   GREATEST(similarity(lower(c.name), lower(:query)) * 2,
                            similarity(lower(c.description), lower(:query))) AS score
            FROM container c
            WHERE similarity(lower(c.name), lower(:query)) > 0.1
               OR similarity(lower(c.description), lower(:query)) > 0.1

            UNION ALL

            SELECT i.id AS id,
                   'ITEM' AS type,
                   i.name AS name,
                   i.description AS description,
                   GREATEST(similarity(lower(i.name), lower(:query)) * 2,
                            similarity(lower(i.description), lower(:query))) AS score
            FROM item i
            WHERE similarity(lower(i.name), lower(:query)) > 0.1
               OR similarity(lower(i.description), lower(:query)) > 0.1
        ) results
        ORDER BY score DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<SearchResponseDto[]> globalSearch(@Param("query") String query,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);
}
