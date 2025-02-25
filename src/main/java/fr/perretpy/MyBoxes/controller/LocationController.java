package fr.perretpy.MyBoxes.controller;

import fr.perretpy.MyBoxes.exception.InvalidPageRequestException;
import fr.perretpy.MyBoxes.exception.InvalidParameterException;
import fr.perretpy.MyBoxes.exception.ResourceNotFoundException;
import fr.perretpy.MyBoxes.exception.InternalServerErrorException;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/locations")
@PreAuthorize("hasAuthority('ROLE_myboxes_user')")
public class LocationController {
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Location>>> getAllLocations(Pageable pageable) {
        logger.info("GET request on /api/locations with parameters: {}", pageable.toString());

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

        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Location>> getLocationById(@PathVariable UUID id) {
        logger.info("GET request on /api/locations/{}", id);

        Location location = locationService.getLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        EntityModel<Location> resource = EntityModel.of(location);
        resource.add(linkTo(methodOn(LocationController.class).getLocationById(id)).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Location>> createLocation(@RequestBody Location location) {
        logger.info("POST request on /api/locations");

        Location savedLocation = locationService.createLocation(location);

        EntityModel<Location> resource = EntityModel.of(savedLocation);
        resource.add(linkTo(methodOn(LocationController.class).getLocationById(savedLocation.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Location>> updateLocation(@PathVariable UUID id, @RequestBody Location location) {
        logger.info("PUT request on /api/locations/{}", id);

        Location updatedLocation = locationService.updateLocation(id, location);

        EntityModel<Location> resource = EntityModel.of(updatedLocation);
        resource.add(linkTo(methodOn(LocationController.class).getLocationById(updatedLocation.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") UUID id) {
        logger.info("DELETE request on /api/locations/{}", id);

        locationService.deleteLocation(id);

        return ResponseEntity.noContent().build();
    }
}
