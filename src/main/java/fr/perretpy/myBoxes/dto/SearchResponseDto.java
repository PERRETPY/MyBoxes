package fr.perretpy.myBoxes.dto;

import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Relation(collectionRelation = "results")
public class SearchResponseDto {
    private UUID id;
    private String type;
    private String title;
    private String description;

    public static SearchResponseDto fromLocation(Location location) {
        return new SearchResponseDto(
                location.getId(),
                "location",
                location.getName(),
                location.getDescription()
        );
    }

    public static SearchResponseDto fromContainer(Container container) {
        return new SearchResponseDto(
                container.getId(),
                "container",
                container.getName(),
                container.getName()
        );
    }

    public static SearchResponseDto fromItem(Item item) {
        return new SearchResponseDto(
                item.getId(),
                "item",
                item.getName(),
                item.getDescription()
        );
    }
}
