package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.service.ItemService;
import fr.perretpy.myBoxes.util.HateoasUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class ItemController extends GenericController<ItemService, Item, UUID>{
    @Autowired
    public ItemController(ItemService itemService) {
        super(itemService);
    }

    @GetMapping("/container/{id}")
    public ResponseEntity<PagedModel<EntityModel<Item>>> getAllItemsByContainerId(@PathVariable UUID id, Pageable pageable, PagedResourcesAssembler<Item> assembler) {
        logger.info("GET request on /api/items/container/{} with parameters: {}", id, pageable);

        Page<Item> itemsPage = service.getAllItemsByContainerId(id, pageable);

        return ResponseEntity.ok(
                assembler.toModel(itemsPage, HateoasUtil::toEntityModel)
        );
    }
}
