package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.GenericModel;
import fr.perretpy.myBoxes.service.GenericService;
import fr.perretpy.myBoxes.util.HateoasUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public abstract class GenericController<S extends GenericService<T, ID>, T extends GenericModel<T, ID>, ID> {
    public final S service;
    public final String requestMapping;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GenericController(S service) {
        this.service = service;
        Class<?> controllerClass = this.getClass();
        if (controllerClass.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = controllerClass.getAnnotation(RequestMapping.class);
            String[] paths = mapping.value();
            if (paths.length > 0) {
                this.requestMapping = paths[0];
            }else {
                this.requestMapping = "no request mapping";
            }
        }else {
            this.requestMapping = "no request mapping";
        }
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<T>>> getAll(Pageable pageable, PagedResourcesAssembler<T> assembler) {
        logger.info("GET request on {} with parameters: {}", requestMapping, pageable);

        Page<T> tPages = service.getAll(pageable);

        return ResponseEntity.ok(
                assembler.toModel(tPages, HateoasUtil::toEntityModel)
        );
    }

    @GetMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<EntityModel<T>> getById(@PathVariable ID id) {
        logger.info("GET request on {}/{}", requestMapping, id);
        T object = service.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        EntityModel<T> resource = EntityModel.of(object);
        resource.add(linkTo(methodOn(this.getClass()).getById(id)).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<EntityModel<T>> create(@RequestBody T object) {
        logger.info("POST request on {}", requestMapping);

        T savedObject = service.create(object);

        EntityModel<T> resource = EntityModel.of(savedObject);
        resource.add(linkTo(methodOn(this.getClass()).getById(savedObject.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<EntityModel<T>> update(@PathVariable ID id, @RequestBody T object) {
        logger.info("PUT request on {}/{}", requestMapping, id);

        T updatedObject = service.update(id, object);

        EntityModel<T> resource = EntityModel.of(updatedObject);
        resource.add(linkTo(methodOn(this.getClass()).getById(updatedObject.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") ID id) {
        logger.info("DELETE request on {}/{}", requestMapping, id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "q")
    public ResponseEntity<PagedModel<EntityModel<T>>> search(@RequestParam("q") String query, Pageable pageable, PagedResourcesAssembler<T> assembler) {
        logger.info("GET request on {} with parameters: {} and {}", requestMapping, query, pageable);

        Page<T> tPages = service.search(query, pageable);

        return ResponseEntity.ok(
                assembler.toModel(tPages, HateoasUtil::toEntityModel)
        );
    }
}
