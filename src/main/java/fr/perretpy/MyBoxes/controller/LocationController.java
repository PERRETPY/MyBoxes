package fr.perretpy.MyBoxes.controller;

import fr.perretpy.MyBoxes.model.Location;
import fr.perretpy.MyBoxes.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/api/location")
public class LocationController {
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    @Value("${api.version}")
    String apiVersion;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Location>>> getAllLocations(Pageable pageable) {
        logger.info("GET request on /api/location with parameters : " + pageable.toString());
        Page<Location> locationsPage = locationService.getAllLocations(pageable);

        List<EntityModel<Location>> locationResources = locationsPage.stream()
                .map(location -> {
                    EntityModel<Location> resource = EntityModel.of(location);
                    Link selfLink = linkTo(methodOn(LocationController.class)
                            .getLocationById(location.getId())).withSelfRel();
                    resource.add(selfLink);
                    return resource;
                })
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(LocationController.class)
                .getAllLocations(pageable)).withSelfRel();

        Link nextLink = locationsPage.hasNext() ?
                linkTo(methodOn(LocationController.class)
                        .getAllLocations(pageable.next())).withRel("next") : null;

        Link prevLink = locationsPage.hasPrevious() ?
                linkTo(methodOn(LocationController.class)
                        .getAllLocations(pageable.previousOrFirst())).withRel("prev") : null;

        PagedModel<EntityModel<Location>> pagedModel = PagedModel.of(
                locationResources,
                new PagedModel.PageMetadata(
                        locationsPage.getSize(),
                        locationsPage.getNumber(),
                        locationsPage.getTotalElements(),
                        locationsPage.getTotalPages()
                ),
                Stream.of(selfLink, nextLink, prevLink)
                        .filter(Objects::nonNull)
                        .toList()
        );

        HttpStatus httpStatus = pagedModel.getContent().isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(pagedModel, httpStatus);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Location>> getLocationById(@PathVariable UUID id) {
        Location location = locationService.getLocation(id);
        EntityModel<Location> resource = EntityModel.of(location);
        resource.add(linkTo(methodOn(LocationController.class).getLocationById(id)).withSelfRel());
        return ResponseEntity.ok(resource);
    }
}
