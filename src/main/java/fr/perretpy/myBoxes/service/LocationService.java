package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidPageRequestException;
import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Service
public class LocationService extends GenericService<Location, UUID>{
    private final LocationRepository locationRepository;
    private final ContainerService containerService;

    @Autowired
    public LocationService(LocationRepository locationRepository, ContainerService containerService) {
        super(locationRepository);
        this.locationRepository = locationRepository;
        this.containerService = containerService;
    }

    public Page<Location> getAllLocations(Pageable pageable) throws InvalidPageRequestException {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPageRequestException("Page ou taille de page invalide");
        }

        return locationRepository.findAll(pageable);
    }

    public Optional<Location> getLocation(UUID id) throws InvalidParameterException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }
        return this.locationRepository.findById(id);
    }

    public Location createLocation(Location location) throws InvalidParameterException {
        if(location == null) {
            throw new InvalidParameterException("location cannot be null");
        }
        if(location.getName().isBlank()) {
            throw new InvalidParameterException("location name cannot be null");
        }
        location.setId(null);
        return this.locationRepository.save(location);
    }

    public Location updateLocation(UUID id, Location location) throws InvalidParameterException {
        return update(id, location, (existing, newEntity) -> {
            if (newEntity.getName() != null && !newEntity.getName().isBlank()) {
                existing.setName(newEntity.getName());
            }
            if (newEntity.getDescription() != null && !newEntity.getDescription().isBlank()) {
                existing.setDescription(newEntity.getDescription());
            }
        });
    }

    @Transactional
    public void deleteLocation(UUID id) throws InvalidParameterException, ResourceNotFoundException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        this.containerService.deleteContainerByLocationId(id);
        locationRepository.delete(location);
    }
}