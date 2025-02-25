package fr.perretpy.MyBoxes.service;

import fr.perretpy.MyBoxes.exception.InvalidPageRequestException;
import fr.perretpy.MyBoxes.exception.InvalidParameterException;
import fr.perretpy.MyBoxes.exception.ResourceNotFoundException;
import fr.perretpy.MyBoxes.model.Location;
import fr.perretpy.MyBoxes.repository.LocationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Data
@Service
public class LocationService {
    private LocationRepository locationRepository;

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

    public Location getLocation(UUID id) throws InvalidParameterException, ResourceNotFoundException {
        System.out.println(id);
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }
        if(this.locationRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Location not found");
        }
        return this.locationRepository.findById(id).get();
    }
}