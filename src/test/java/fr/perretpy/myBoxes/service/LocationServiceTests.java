package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {
   /* @Mock
    ContainerService containerService;

    @Mock
    LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    // getAllLocations TEST
    @Test
    void test_getAllLocations_WhenValidPageable_ShouldReturnPage() {
        Location location1 = new Location(UUID.randomUUID(), "Garage", "");
        Location location2 = new Location(UUID.randomUUID(), "Salon", "");
        List<Location> locations = List.of(location1, location2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Location> expectedPage = new PageImpl<>(locations, pageable, locations.size());

        when(locationRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Location> result = locationService.getAllLocations(pageable);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Garage", result.getContent().get(0).getName());
        assertEquals("Salon", result.getContent().get(1).getName());

        verify(locationRepository, times(1)).findAll(pageable);
    }

    @Test
    void test_getAllLocations_WhenValidPageableNoContent_ShouldReturnPageEmptyContent() {
        List<Location> locations = List.of();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Location> expectedPage = new PageImpl<>(locations, pageable, 0);

        when(locationRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Location> result = locationService.getAllLocations(pageable);
        assertNotNull(result);
        assertEquals(0, result.getContent().size());

        verify(locationRepository, times(1)).findAll(pageable);
    }

     // updateLocation TEST

    @Test
    void test_updateLocation_WhenValidUpdateLocationAndId_ShouldReturnNewLocation() {
        Location prevLocation = new Location(UUID.randomUUID(), "Garagge", "Prev description");
        Location newLocation = new Location(UUID.randomUUID(), "Garage", "New description");

        when(locationRepository.findById(prevLocation.getId())).thenReturn(Optional.of(prevLocation));
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location updatedLocation = locationService.updateLocation(prevLocation.getId(), newLocation);

        assertEquals(newLocation.getName(), updatedLocation.getName());
        assertEquals(newLocation.getDescription(), updatedLocation.getDescription());
        assertEquals(prevLocation.getId(), updatedLocation.getId());

        verify(locationRepository, times(1)).findById(prevLocation.getId());
    }

    @Test
    void test_updateLocation_WhenValidUpdateLocationNoExistingId_ShouldReturnException() {
        Location prevLocation = new Location(UUID.randomUUID(), "Garagge", "Prev description");
        Location newLocation = new Location(UUID.randomUUID(), "Garage", "New description");

        when(locationRepository.findById(prevLocation.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvalidParameterException.class, () -> locationService.updateLocation(prevLocation.getId(), newLocation));

        assertEquals(exception.getMessage(), "Entity with ID " + prevLocation.getId() + " doesn't exist");

        verify(locationRepository, times(1)).findById(prevLocation.getId());
        verify(locationRepository, times(0)).save(any(Location.class));
    }

     // deleteLocation TEST

    @Test
    void test_deleteLocation_WhenValidId_ShouldCallContainerDeleteCascadeAndDeleteRepository() {
        Location location = new Location(UUID.randomUUID(), "Garage", "Description");

        when(locationRepository.findById(location.getId())).thenReturn(Optional.of(location));

        locationService.deleteLocation(location.getId());

        verify(locationRepository, times(1)).findById(location.getId());
        verify(containerService, times(1)).deleteContainerByLocationId(location.getId());
        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    void test_deleteLocation_WhenNoExistingId_ShouldReturnException() {
        Location location = new Location(UUID.randomUUID(), "Garage", "Description");

        when(locationRepository.findById(location.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation(location.getId()));

        assertEquals(exception.getMessage(), "Location not found");

        verify(locationRepository, times(1)).findById(location.getId());
        verify(containerService, times(0)).deleteContainerByLocationId(location.getId());
        verify(locationRepository, times(0)).delete(location);
    }*/
}
