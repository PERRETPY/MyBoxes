package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class LocationController extends GenericController<LocationService, Location, UUID> {

    @Autowired
    public LocationController(LocationService locationService) {
        super(locationService);
    }
}
