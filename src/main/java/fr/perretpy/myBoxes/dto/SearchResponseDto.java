package fr.perretpy.myBoxes.dto;

import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Relation(collectionRelation = "results")
public class SearchResponseDto {
    private UUID id;
    private String type;
    private String name;
    private String description;
    private double score;

    public static SearchResponseDto fromContainer(Container container) {
        return new SearchResponseDto(
                container.getId(),
                "container",
                container.getName(),
                container.getDescription(),
                0
        );
    }

    public static SearchResponseDto fromItem(Item item) {
        return new SearchResponseDto(
                item.getId(),
                "item",
                item.getName(),
                item.getDescription(),
                0
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResponseDto that)) return false;
        return Objects.equals(type, that.type) &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }
}
