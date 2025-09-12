package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.repository.ContainerRepository;
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

    @InjectMocks
    private ContainerService containerService;

    // getAllLocations TEST
    @Test
    void test_getAllContainers_WhenValidPageable_ShouldReturnPage() {
        Container containerParent = new Container(UUID.randomUUID(), "Garage", "Pièce", null);
        Container container1 = new Container(UUID.randomUUID(), "Boite transparente 1", "Boite", containerParent);
        Container container2 = new Container(UUID.randomUUID(), "Boite transparente 2", "Boite", containerParent);
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
        Container containerParent = new Container(UUID.randomUUID(), "Garage", "Pièce", null);
        Container prevContainer = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", containerParent);
        Container newContainer = new Container(UUID.randomUUID(), "Nouveau nom", "Boite", containerParent);


        when(containerRepository.findById(containerParent.getId())).thenReturn(Optional.of(containerParent));
        when(containerRepository.findById(prevContainer.getId())).thenReturn(Optional.of(prevContainer));
        when(containerRepository.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Container updatedContainer = containerService.update(prevContainer.getId(), newContainer);

        assertEquals(newContainer.getName(), updatedContainer.getName());
        assertEquals(newContainer.getDescription(), updatedContainer.getDescription());
        assertEquals(prevContainer.getId(), updatedContainer.getId());

        verify(containerRepository, times(1)).findById(prevContainer.getId());
    }

    @Test
    void test_updateLocation_WhenValidUpdateLocationNoExistingId_ShouldReturnException() {
        Container containerParent = new Container(UUID.randomUUID(), "Garage", "Pièce", null);
        Container prevContainer = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", containerParent);
        Container newContainer = new Container(UUID.randomUUID(), "Nouveau nom", "Boite", containerParent);

        Exception exception = assertThrows(InvalidParameterException.class, () -> containerService.update(prevContainer.getId(), newContainer));

        assertEquals(exception.getMessage(), "Entity with ID " + prevContainer.getId() + " doesn't exist");

        verify(containerRepository, times(1)).findById(prevContainer.getId());
        verify(containerRepository, times(0)).save(any(Container.class));
    }

    @Test
    void test_updateLocation_WhenNoValidUpdateLocationExistingId_ShouldReturnException() {
        Container containerParent = new Container(UUID.randomUUID(), "Garage", "Pièce", null);
        Container prevContainer = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", containerParent);
        Container newContainer = new Container(UUID.randomUUID(), "Nouveau nom", "Boite", containerParent);

        when(containerRepository.findById(prevContainer.getId())).thenReturn(Optional.of(prevContainer));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> containerService.update(prevContainer.getId(), newContainer));

        assertEquals(exception.getMessage(), "Location not found");

        verify(containerRepository, times(1)).findById(prevContainer.getId());
        verify(containerRepository, times(0)).save(any(Container.class));
    }

     // TODO : deleteLocation TEST

    @Test
    void test_deleteLocation_WhenValidId_ShouldCallContainerDeleteCascadeAndDeleteRepository() {
        Container containerParent = new Container(UUID.randomUUID(), "Garage", "Pièce", null);
        Container container = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", containerParent);

        when(containerRepository.findById(container.getId())).thenReturn(Optional.of(container));

        containerService.delete(container.getId());

        verify(containerRepository, times(1)).findById(container.getId());
        verify(itemService, times(1)).deleteItemByContainerId(container.getId());
        verify(containerRepository, times(1)).delete(container);
    }

    @Test
    void test_deleteLocation_WhenNoExistingId_ShouldReturnException() {
        Container containerParent = new Container(UUID.randomUUID(), "Garage", "Pièce", null);
        Container container = new Container(UUID.randomUUID(), "Ancien nom", "Boitte", containerParent);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> containerService.delete(container.getId()));

        assertEquals(exception.getMessage(), "Container not found");

        verify(containerRepository, times(1)).findById(container.getId());
        verify(itemService, times(0)).deleteItemByContainerId(container.getId());
        verify(containerRepository, times(0)).delete(container);
    }
}
