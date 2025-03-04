package fr.perretpy.myBoxes.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Table(name = "location")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location extends RepresentationModel<Location> implements GenericModel<Location, UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}
