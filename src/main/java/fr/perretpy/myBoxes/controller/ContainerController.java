package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.service.ContainerService;
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
@RequestMapping("/api/containers")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class ContainerController {
    private final Logger logger = LoggerFactory.getLogger(ContainerController.class);

    private final ContainerService containerService;

    @Autowired
    public ContainerController(ContainerService containerService) {
        this.containerService = containerService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Container>>> getAllContainers(Pageable pageable) {
        logger.info("GET request on /api/containers with parameters: {}", pageable);

        Page<Container> containersPage = containerService.getAllContainers(pageable);

        Link selfLink = linkTo(methodOn(ContainerController.class).getAllContainers(pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                containersPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
    }

    @GetMapping("/location/{id}")
    public ResponseEntity<PagedModel<EntityModel<Container>>> getAllContainersByLocationId(@PathVariable UUID id, Pageable pageable) {
        logger.info("GET request on /api/containers/location/{} with parameters: {}", id, pageable);

        Page<Container> containersPage = containerService.getAllContainersByLocationId(id, pageable);

        Link selfLink = linkTo(methodOn(ContainerController.class).getAllContainersByLocationId(id, pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                containersPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Container>> getContainerById(@PathVariable UUID id) {
        logger.info("GET request on /api/containers/{}", id);

        Container container = containerService.getContainer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Container not found"));

        EntityModel<Container> resource = EntityModel.of(container);
        resource.add(linkTo(methodOn(ContainerController.class).getContainerById(id)).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Container>> createContainer(@RequestBody Container container) {
        logger.info("POST request on /api/containers");

        Container savedContainer = containerService.createContainer(container);

        EntityModel<Container> resource = EntityModel.of(savedContainer);
        resource.add(linkTo(methodOn(ContainerController.class).getContainerById(savedContainer.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Container>> updateContainer(@PathVariable UUID id, @RequestBody Container container) {
        logger.info("PUT request on /api/containers/{}", id);

        Container updatedContainer = containerService.updateContainer(id, container);

        EntityModel<Container> resource = EntityModel.of(updatedContainer);
        resource.add(linkTo(methodOn(ContainerController.class).getContainerById(updatedContainer.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContainer(@PathVariable("id") UUID id) {
        logger.info("DELETE request on /api/containers/{}", id);

        containerService.deleteContainer(id);

        return ResponseEntity.noContent().build();
    }
}
