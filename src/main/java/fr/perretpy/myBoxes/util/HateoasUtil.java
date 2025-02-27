package fr.perretpy.myBoxes.util;

import fr.perretpy.myBoxes.controller.ContainerController;
import fr.perretpy.myBoxes.controller.ItemController;
import fr.perretpy.myBoxes.controller.LocationController;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Location;
import org.springframework.hateoas.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class HateoasUtil {
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

    public static EntityModel<Location> toEntityModel(Location location) {
        EntityModel<Location> resource = EntityModel.of(location);
        resource.add(linkTo(methodOn(LocationController.class).getLocationById(location.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ContainerController.class)
                .getAllContainersByLocationId(location.getId(), Pageable.unpaged())).withRel("ContainerList"));
        return resource;
    }

    public static EntityModel<Container> toEntityModel(Container container) {
        EntityModel<Container> resource = EntityModel.of(container);
        resource.add(linkTo(methodOn(ContainerController.class).getContainerById(container.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ItemController.class).getAllItemsByContainerId(container.getId(), Pageable.unpaged())).withRel("ItemList"));
        return resource;
    }

    public static EntityModel<Item> toEntityModel(Item item) {
        EntityModel<Item> resource = EntityModel.of(item);
        resource.add(linkTo(methodOn(ItemController.class).getItemById(item.getId())).withSelfRel());
        return resource;
    }
}
