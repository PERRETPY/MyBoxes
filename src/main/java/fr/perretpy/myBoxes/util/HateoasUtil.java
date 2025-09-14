package fr.perretpy.myBoxes.util;

import fr.perretpy.myBoxes.controller.ItemController;
import fr.perretpy.myBoxes.controller.*;
import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class HateoasUtil {

    private static final Map<Class<?>, Function<Object, EntityModel<?>>> entityModelMappers = new HashMap<>();

    static {
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

    private static EntityModel<Container> toContainerModel(Container container) {
        EntityModel<Container> resource = EntityModel.of(container);
        resource.add(linkTo(methodOn(ContainerController.class).getById(container.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ContainerController.class).getAllContainersByParentId(container.getId(), Pageable.unpaged(), new PagedResourcesAssembler<>(null, null))).withRel("Childrens"));
        if(container.getParent() != null) {
            resource.add(linkTo(methodOn(ContainerController.class).getById(container.getParent().getId())).withRel("Parent"));
        }
        resource.add(linkTo(methodOn(ItemController.class).getAllItemsByContainerId(container.getId(), Pageable.unpaged(), new PagedResourcesAssembler<>(null, null))).withRel("ItemList"));
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
            case "container" -> resource.add(linkTo(methodOn(ContainerController.class).getById(dto.getId())).withRel("container"));
            case "item" -> resource.add(linkTo(methodOn(ItemController.class).getById(dto.getId())).withRel("item"));
            default -> throw new IllegalArgumentException("Unknown type: " + dto.getType());
        }

        return resource;
    }
}
