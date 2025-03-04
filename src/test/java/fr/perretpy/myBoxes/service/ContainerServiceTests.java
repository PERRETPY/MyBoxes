package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.ContainerRepository;
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
public class ContainerServiceTests {
    @Mock
    ItemService itemService;

    @Mock
    ContainerRepository containerRepository;

    @Mock
    LocationRepository locationRepository;

    @InjectMocks
    private ContainerService containerService;

    // getAllLocations TEST
    @Test
    void test_getAllContainers_WhenValidPageable_ShouldReturnPage() {
        Location location = new Location(UUID.randomUUID(), "Garage", "");
        Container container1 = new Container(UUID.randomUUID(), "Boite transparente 1", "Boite", location);
        Container container2 = new Container(UUID.randomUUID(), "Boite transparente 2", "Boite", location);
        List<Container> containers = List.of(container1, container2);


        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Container> expectedPage = new PageImpl<>(containers, pageable, containers.size());

        when(containerRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Container> result = containerService.getAll(pageable);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Boite transparente 1", result.getContent().get(0).getName());
        assertEquals("Boite transparente 2", result.getContent().get(1).getName());

        verify(containerRepository, times(1)).findAll(pageable);
    }

    @Test
    void test_getAllContainers_WhenValidPageableNoContent_ShouldReturnPageEmptyContent() {
        List<Container> containers = List.of();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Container> expectedPage = new PageImpl<>(containers, pageable, 0);

        when(containerRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Container> result = containerService.getAll(pageable);
        assertNotNull(result);
        assertEquals(0, result.getContent().size());

        verify(containerRepository, times(1)).findAll(pageable);
    }

     // updateLocation TEST

    @Test
    void test_updateContainer_WhenValidUpdateContainerAndId_ShouldReturnNewContainer() {
        Location location = new Location(UUID.randomUUID(), "Garage", "");
        Container prevContainer = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", location);
        Container newContainer = new Container(UUID.randomUUID(), "Nouveau nom", "Boite", location);


        when(locationRepository.findById(newContainer.getLocation().getId())).thenReturn(Optional.of(newContainer.getLocation()));
        when(containerRepository.findById(prevContainer.getId())).thenReturn(Optional.of(prevContainer));
        when(containerRepository.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Container updatedContainer = containerService.update(prevContainer.getId(), newContainer);

        assertEquals(newContainer.getName(), updatedContainer.getName());
        assertEquals(newContainer.getType(), updatedContainer.getType());
        assertEquals(prevContainer.getId(), updatedContainer.getId());

        verify(containerRepository, times(1)).findById(prevContainer.getId());
    }

    @Test
    void test_updateLocation_WhenValidUpdateLocationNoExistingId_ShouldReturnException() {
        Location location = new Location(UUID.randomUUID(), "Garage", "");
        Container prevContainer = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", location);
        Container newContainer = new Container(UUID.randomUUID(), "Nouveau nom", "Boite", location);

        Exception exception = assertThrows(InvalidParameterException.class, () -> containerService.update(prevContainer.getId(), newContainer));

        assertEquals(exception.getMessage(), "Entity with ID " + prevContainer.getId() + " doesn't exist");

        verify(containerRepository, times(1)).findById(prevContainer.getId());
        verify(containerRepository, times(0)).save(any(Container.class));
    }

    @Test
    void test_updateLocation_WhenNoValidUpdateLocationExistingId_ShouldReturnException() {
        Location location = new Location(UUID.randomUUID(), "Garage", "");
        Container prevContainer = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", location);
        Container newContainer = new Container(UUID.randomUUID(), "Nouveau nom", "Boite", location);

        when(containerRepository.findById(prevContainer.getId())).thenReturn(Optional.of(prevContainer));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> containerService.update(prevContainer.getId(), newContainer));

        assertEquals(exception.getMessage(), "Location not found");

        verify(containerRepository, times(1)).findById(prevContainer.getId());
        verify(containerRepository, times(0)).save(any(Container.class));
    }

     // deleteLocation TEST

    /*@Test
    void test_deleteLocation_WhenValidId_ShouldCallContainerDeleteCascadeAndDeleteRepository() {
        Location location = new Location(UUID.randomUUID(), "Garage", "Description");

        when(locationRepository.findById(location.getId())).thenReturn(Optional.of(location));

        locationService.delete(location.getId());

        verify(locationRepository, times(1)).findById(location.getId());
        verify(containerService, times(1)).deleteContainerByLocationId(location.getId());
        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    void test_deleteLocation_WhenNoExistingId_ShouldReturnException() {
        Location location = new Location(UUID.randomUUID(), "Garage", "Description");

        when(locationRepository.findById(location.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.delete(location.getId()));

        assertEquals(exception.getMessage(), "Location not found");

        verify(locationRepository, times(1)).findById(location.getId());
        verify(containerService, times(0)).deleteContainerByLocationId(location.getId());
        verify(locationRepository, times(0)).delete(location);
    }*/
}
