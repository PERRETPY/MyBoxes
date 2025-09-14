package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.service.ContainerService;
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
@RequestMapping("/api/containers")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class ContainerController extends GenericController<ContainerService, Container, UUID> {
    @Autowired
    public ContainerController(ContainerService containerService) {
        super(containerService);
    }

    @GetMapping("/parent/{id}")
    public ResponseEntity<PagedModel<EntityModel<Container>>> getAllContainersByParentId(@PathVariable UUID id, Pageable pageable, PagedResourcesAssembler<Container> assembler) {
        logger.info("GET request on /api/containers/parent/{} with parameters: {}", id, pageable);

        Page<Container> containersPage = service.getAllContainersByParentId(id, pageable);

        return ResponseEntity.ok(
                assembler.toModel(containersPage, HateoasUtil::toEntityModel)
        );
    }
}
