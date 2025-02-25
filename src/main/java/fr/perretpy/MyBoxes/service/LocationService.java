package fr.perretpy.MyBoxes.service;

import fr.perretpy.MyBoxes.exception.InvalidPageRequestException;
import fr.perretpy.MyBoxes.exception.InvalidParameterException;
import fr.perretpy.MyBoxes.exception.ResourceNotFoundException;
import fr.perretpy.MyBoxes.model.Location;
import fr.perretpy.MyBoxes.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Data
@Service
public class LocationService {
    private LocationRepository locationRepository;
    private ContainerService containerService;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
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
        Optional<Location> optionalExistedLocation = getLocation(id);
        Location existedLocation = new Location();
        if (optionalExistedLocation.isPresent()) {
            existedLocation = optionalExistedLocation.get();
        }else {
            throw new InvalidParameterException("room id doesn't exist");
        }

        if(location.getName() != null && !location.getName().isBlank()) {
            existedLocation.setName(location.getName());
        }

        if(location.getDescription() != null && !location.getDescription().isBlank()) {
            existedLocation.setDescription(location.getDescription());
        }

        return locationRepository.save(existedLocation);
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