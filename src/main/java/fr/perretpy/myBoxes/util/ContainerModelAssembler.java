package fr.perretpy.myBoxes.util;

import fr.perretpy.myBoxes.model.Container;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ContainerModelAssembler implements RepresentationModelAssembler<Container, EntityModel<Container>> {

    @Override
    public EntityModel<Container> toModel(Container container) {
        return null;
    }
    }
