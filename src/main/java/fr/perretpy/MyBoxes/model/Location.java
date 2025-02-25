package fr.perretpy.MyBoxes.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Table(name = "location")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location extends RepresentationModel<Location> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "location_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}
