package fr.perretpy.myBoxes.util;

import fr.perretpy.myBoxes.controller.ContainerController;
import fr.perretpy.myBoxes.controller.ItemController;
import fr.perretpy.myBoxes.controller.LocationController;
import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Location;
import org.springframework.hateoas.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class HateoasUtil {

    private static final Map<Class<?>, Function<Object, EntityModel<?>>> entityModelMappers = new HashMap<>();

    static {
        entityModelMappers.put(Location.class, entity -> toLocationModel((Location) entity));
        entityModelMappers.put(Container.class, entity -> toContainerModel((Container) entity));
        entityModelMappers.put(Item.class, entity -> toItemModel((Item) entity));
        entityModelMappers.put(SearchResponseDto.class, entity -> toSearchResponseDtoModel((SearchResponseDto) entity));
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityModel<T> toEntityModel(T entity) {
        Function<Object, EntityModel<?>> mapper = entityModelMappers.get(entity.getClass());
        if (mapper != null) {
            return (EntityModel<T>) mapper.apply(entity);
        }
        throw new IllegalArgumentException("No HATEOAS mapper found for " + entity.getClass().getSimpleName());
    }

    public static <T> PagedModel<EntityModel<T>> toPagedModel(
            Page<T> page,
            Pageable pageable,
            Function<T, EntityModel<T>> entityModelMapper,
            Link selfLink) {

        List<EntityModel<T>> resources = page.stream()
                .map(entityModelMapper)
                .collect(Collectors.toList());

        Link nextLink = page.hasNext() ?
                selfLink.withRel("next").expand(pageable.next()) : null;

        Link prevLink = page.hasPrevious() ?
                selfLink.withRel("prev").expand(pageable.previousOrFirst()) : null;

        return PagedModel.of(
                resources,
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                Stream.of(selfLink, nextLink, prevLink)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    private static EntityModel<Location> toLocationModel(Location location) {
        EntityModel<Location> resource = EntityModel.of(location);
        resource.add(linkTo(methodOn(LocationController.class).getById(location.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ContainerController.class)
                .getAllContainersByLocationId(location.getId(), Pageable.unpaged())).withRel("ContainerList"));
        return resource;
    }

    private static EntityModel<Container> toContainerModel(Container container) {
        EntityModel<Container> resource = EntityModel.of(container);
        resource.add(linkTo(methodOn(ContainerController.class).getById(container.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ItemController.class).getAllItemsByContainerId(container.getId(), Pageable.unpaged())).withRel("ItemList"));
        return resource;
    }

    private static EntityModel<Item> toItemModel(Item item) {
        EntityModel<Item> resource = EntityModel.of(item);
        resource.add(linkTo(methodOn(ItemController.class).getById(item.getId())).withSelfRel());
        return resource;
    }

    private static EntityModel<SearchResponseDto> toSearchResponseDtoModel(SearchResponseDto dto) {
        EntityModel<SearchResponseDto> resource = EntityModel.of(dto);

        switch (dto.getType()) {
            case "location" -> resource.add(linkTo(methodOn(LocationController.class).getById(dto.getId())).withRel("location"));
            case "container" -> resource.add(linkTo(methodOn(ContainerController.class).getById(dto.getId())).withRel("container"));
            case "item" -> resource.add(linkTo(methodOn(ItemController.class).getById(dto.getId())).withRel("item"));
            default -> throw new IllegalArgumentException("Unknown type: " + dto.getType());
        }

        return resource;
    }
}
