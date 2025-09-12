package fr.perretpy.myBoxes.util;

import fr.perretpy.myBoxes.controller.ContainerController;
import fr.perretpy.myBoxes.controller.ItemController;
import fr.perretpy.myBoxes.model.Container;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ContainerModelAssembler implements RepresentationModelAssembler<Container, EntityModel<Container>> {

    @Override
    public EntityModel<Container> toModel(Container container) {
        return null;
    }
    }
