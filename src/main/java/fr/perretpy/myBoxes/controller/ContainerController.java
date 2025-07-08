package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.service.ContainerService;
import fr.perretpy.myBoxes.util.HateoasUtil;
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
@RequestMapping("/api/containers")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class ContainerController extends GenericController<ContainerService, Container, UUID> {
    @Autowired
    public ContainerController(ContainerService containerService) {
        super(containerService);
    }

    @GetMapping("/location/{id}")
    public ResponseEntity<PagedModel<EntityModel<Container>>> getAllContainersByLocationId(@PathVariable UUID id, Pageable pageable) {
        logger.info("GET request on /api/containers/location/{} with parameters: {}", id, pageable);

        Page<Container> containersPage = service.getAllContainersByLocationId(id, pageable);

        Link selfLink = linkTo(methodOn(ContainerController.class).getAllContainersByLocationId(id, pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                containersPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
    }
}
