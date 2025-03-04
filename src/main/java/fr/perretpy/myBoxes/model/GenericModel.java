package fr.perretpy.myBoxes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;



public interface GenericModel<T, ID> {
    public ID getId();
    public void setId(ID id);
}
