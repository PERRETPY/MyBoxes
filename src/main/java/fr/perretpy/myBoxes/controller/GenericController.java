package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.GenericModel;
import fr.perretpy.myBoxes.service.GenericService;
import fr.perretpy.myBoxes.util.HateoasUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
public abstract class GenericController<S extends GenericService<T, ID>, T extends GenericModel<T, ID>, ID> {
    public final S service;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<T>>> getAll(Pageable pageable) {
        //logger.info("GET request on /api/locations with parameters: {}", pageable);

        Page<T> tPages = service.getAll(pageable);

        Link selfLink = linkTo(methodOn(this.getClass()).getAll(pageable)).withSelfRel();

        return ResponseEntity.ok(HateoasUtil.toPagedModel(
                tPages,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<T>> getById(@PathVariable ID id) {
        //logger.info("GET request on /api/locations/{}", id);
        T object = service.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        EntityModel<T> resource = EntityModel.of(object);
        resource.add(linkTo(methodOn(this.getClass()).getById(id)).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<EntityModel<T>> createLocation(@RequestBody T object) {
        //logger.info("POST request on /api/locations");

        T savedObject = service.create(object);

        EntityModel<T> resource = EntityModel.of(savedObject);
        resource.add(linkTo(methodOn(this.getClass()).getById(savedObject.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<T>> updateLocation(@PathVariable ID id, @RequestBody T object) {
        logger.info("PUT request on /api/locations/{}", id);

        T updatedObject = service.update(id, object);

        EntityModel<T> resource = EntityModel.of(updatedObject);
        resource.add(linkTo(methodOn(this.getClass()).getById(updatedObject.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") ID id) {
        //logger.info("DELETE request on /api/locations/{}", id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
