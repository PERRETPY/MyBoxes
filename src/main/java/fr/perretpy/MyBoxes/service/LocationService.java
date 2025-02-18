package fr.perretpy.MyBoxes.service;

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

    public Page<Location> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    public Location getLocation(UUID id) {
        return this.locationRepository.findById(id).isPresent() ? this.locationRepository.findById(id).get() : null;
    }
}