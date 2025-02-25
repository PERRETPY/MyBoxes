package fr.perretpy.MyBoxes.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.UUID;

@Table(name = "container")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Container extends RepresentationModel<Container> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "container_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private String type;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(optional = false)
    @JoinColumn(name = "location_fk")
    private Location location;
}
