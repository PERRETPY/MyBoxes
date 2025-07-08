package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Service
public class LocationService extends GenericService<Location, UUID> {
    private final ContainerService containerService;

    @Autowired
    public LocationService(LocationRepository locationRepository, ContainerService containerService) {
        super(locationRepository);
        this.containerService = containerService;
    }

    @Override
    public Location create(Location location) throws InvalidParameterException {
        return createWithCreater(location, (newLocation) -> {
            if(newLocation == null) {
                throw new InvalidParameterException("location cannot be null");
            }
            if(newLocation.getName().isBlank()) {
                throw new InvalidParameterException("location name cannot be null");
            }
        });
    }

    @Override
    public Location update(UUID id, Location location) throws InvalidParameterException {
        return updateWithUpdater(id, location, (existing, newEntity) -> {
            if (newEntity.getName() != null && !newEntity.getName().isBlank()) {
                existing.setName(newEntity.getName());
            }
            if (newEntity.getDescription() != null && !newEntity.getDescription().isBlank()) {
                existing.setDescription(newEntity.getDescription());
            }
        });
    }

    @Transactional
    @Override
    public void delete(UUID id) throws InvalidParameterException, ResourceNotFoundException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        Location location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        this.containerService.deleteContainerByLocationId(id);
        repository.delete(location);
    }

    @Override
    public Page<Location> search(String query, Pageable pageable) {
        LocationRepository locationRepository = (LocationRepository) repository;
        return locationRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
    }
}