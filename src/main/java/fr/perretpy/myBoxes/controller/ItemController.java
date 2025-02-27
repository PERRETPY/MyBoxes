package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.service.ItemService;
import fr.perretpy.myBoxes.util.HateoasUtil;
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

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class ItemController {
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Item>>> getAllItems(Pageable pageable) {
        logger.info("GET request on /api/items with parameters: {}", pageable);

        Page<Item> itemsPage = itemService.getAllItems(pageable);

        Link selfLink = linkTo(methodOn(ItemController.class).getAllItems(pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                itemsPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
    }

    @GetMapping("/container/{id}")
    public ResponseEntity<PagedModel<EntityModel<Item>>> getAllItemsByContainerId(@PathVariable UUID id, Pageable pageable) {
        logger.info("GET request on /api/items/container/{} with parameters: {}", id, pageable);

        Page<Item> itemsPage = itemService.getAllItemsByContainerId(id, pageable);

        Link selfLink = linkTo(methodOn(ItemController.class).getAllItemsByContainerId(id, pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                itemsPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
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
