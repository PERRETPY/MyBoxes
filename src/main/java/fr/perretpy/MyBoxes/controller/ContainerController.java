package fr.perretpy.MyBoxes.controller;

import fr.perretpy.MyBoxes.exception.ResourceNotFoundException;
import fr.perretpy.MyBoxes.model.Container;
import fr.perretpy.MyBoxes.service.ContainerService;
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
@RequestMapping("/api/containers")
@PreAuthorize("hasAuthority('ROLE_myboxes_user')")
public class ContainerController {
    private final Logger logger = LoggerFactory.getLogger(ContainerController.class);

    private final ContainerService containerService;

    @Autowired
    public ContainerController(ContainerService containerService) {
        this.containerService = containerService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Container>>> getAllContainers(Pageable pageable) {
        logger.info("GET request on /api/containers with parameters: {}", pageable.toString());

        Page<Container> containersPage = containerService.getAllContainers(pageable);

        List<EntityModel<Container>> containerResources = containersPage.stream()
                .map(container -> {
                    EntityModel<Container> resource = EntityModel.of(container);
                    Link selfLink = linkTo(methodOn(ContainerController.class)
                            .getContainerById(container.getId())).withSelfRel();
                    resource.add(selfLink);
                    return resource;
                })
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ContainerController.class)
                .getAllContainers(pageable)).withSelfRel();

        Link nextLink = containersPage.hasNext() ?
                linkTo(methodOn(ContainerController.class)
                        .getAllContainers(pageable.next())).withRel("next") : null;

        Link prevLink = containersPage.hasPrevious() ?
                linkTo(methodOn(ContainerController.class)
                        .getAllContainers(pageable.previousOrFirst())).withRel("prev") : null;

        PagedModel<EntityModel<Container>> pagedModel = PagedModel.of(
                containerResources,
                new PagedModel.PageMetadata(
                        containersPage.getSize(),
                        containersPage.getNumber(),
                        containersPage.getTotalElements(),
                        containersPage.getTotalPages()
                ),
                Stream.of(selfLink, nextLink, prevLink)
                        .filter(Objects::nonNull)
                        .toList()
        );

        return ResponseEntity.ok(pagedModel);
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
