package fr.perretpy.MyBoxes.controller;

import fr.perretpy.MyBoxes.exception.ResourceNotFoundException;
import fr.perretpy.MyBoxes.model.Item;
import fr.perretpy.MyBoxes.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAuthority('ROLE_myboxes_user')")
public class ItemController {
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Item>>> getAllItems(Pageable pageable) {
        logger.info("GET request on /api/items with parameters: {}", pageable.toString());

        Page<Item> itemsPage = itemService.getAllItems(pageable);

        List<EntityModel<Item>> itemResources = itemsPage.stream()
                .map(item -> {
                    EntityModel<Item> resource = EntityModel.of(item);
                    Link selfLink = linkTo(methodOn(ItemController.class)
                            .getItemById(item.getId())).withSelfRel();
                    resource.add(selfLink);
                    return resource;
                })
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ItemController.class)
                .getAllItems(pageable)).withSelfRel();

        Link nextLink = itemsPage.hasNext() ?
                linkTo(methodOn(ItemController.class)
                        .getAllItems(pageable.next())).withRel("next") : null;

        Link prevLink = itemsPage.hasPrevious() ?
                linkTo(methodOn(ItemController.class)
                        .getAllItems(pageable.previousOrFirst())).withRel("prev") : null;

        PagedModel<EntityModel<Item>> pagedModel = PagedModel.of(
                itemResources,
                new PagedModel.PageMetadata(
                        itemsPage.getSize(),
                        itemsPage.getNumber(),
                        itemsPage.getTotalElements(),
                        itemsPage.getTotalPages()
                ),
                Stream.of(selfLink, nextLink, prevLink)
                        .filter(Objects::nonNull)
                        .toList()
        );

        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Item>> getItemById(@PathVariable UUID id) {
        logger.info("GET request on /api/items/{}", id);

        Item item = itemService.getItem(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        EntityModel<Item> resource = EntityModel.of(item);
        resource.add(linkTo(methodOn(ItemController.class).getItemById(id)).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Item>> createItem(@RequestBody Item item) {
        logger.info("POST request on /api/items");

        Item savedItem = itemService.createItem(item);

        EntityModel<Item> resource = EntityModel.of(savedItem);
        resource.add(linkTo(methodOn(ItemController.class).getItemById(savedItem.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Item>> updateItem(@PathVariable UUID id, @RequestBody Item item) {
        logger.info("PUT request on /api/items/{}", id);

        Item updatedItem = itemService.updateItem(id, item);

        EntityModel<Item> resource = EntityModel.of(updatedItem);
        resource.add(linkTo(methodOn(ItemController.class).getItemById(updatedItem.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") UUID id) {
        logger.info("DELETE request on /api/items/{}", id);

        itemService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}
