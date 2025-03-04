package fr.perretpy.myBoxes.controller;

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
public class ItemController extends GenericController<ItemService, Item, UUID>{
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);


    @Autowired
    public ItemController(ItemService itemService) {
        super(itemService);
    }

    @GetMapping("/container/{id}")
    public ResponseEntity<PagedModel<EntityModel<Item>>> getAllItemsByContainerId(@PathVariable UUID id, Pageable pageable) {
        logger.info("GET request on /api/items/container/{} with parameters: {}", id, pageable);

        Page<Item> itemsPage = service.getAllItemsByContainerId(id, pageable);

        Link selfLink = linkTo(methodOn(ItemController.class).getAllItemsByContainerId(id, pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                itemsPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
    }
}
