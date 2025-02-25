package fr.perretpy.MyBoxes.service;

import fr.perretpy.MyBoxes.exception.InvalidPageRequestException;
import fr.perretpy.MyBoxes.exception.InvalidParameterException;
import fr.perretpy.MyBoxes.exception.ResourceNotFoundException;
import fr.perretpy.MyBoxes.model.Container;
import fr.perretpy.MyBoxes.model.Location;
import fr.perretpy.MyBoxes.repository.ContainerRepository;
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
public class ContainerService {
    private ContainerRepository containerRepository;
    private LocationRepository locationRepository;
    private ItemService itemService;

    @Autowired
    public ContainerService(ContainerRepository containerRepository, LocationRepository locationRepository, ItemService itemService) {
        this.containerRepository = containerRepository;
        this.locationRepository = locationRepository;
        this.itemService = itemService;
    }

    public Page<Container> getAllContainers(Pageable pageable) throws InvalidPageRequestException {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPageRequestException("Page ou taille de page invalide");
        }

        return containerRepository.findAll(pageable);
    }

    public Optional<Container> getContainer(UUID id) throws InvalidParameterException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }
        return this.containerRepository.findById(id);
    }

    public Container createContainer(Container container) throws InvalidParameterException {
        if(container == null) {
            throw new InvalidParameterException("container cannot be null");
        }
        if(container.getName().isBlank()) {
            throw new InvalidParameterException("container name cannot be null");
        }
        container.setId(null);
        return this.containerRepository.save(container);
    }

    public Container updateContainer(UUID id, Container container) throws InvalidParameterException {
        Optional<Container> optionalExistedContainer = getContainer(id);
        Container existedContainer = new Container();
        if (optionalExistedContainer.isPresent()) {
            existedContainer = optionalExistedContainer.get();
        }else {
            throw new InvalidParameterException("room id doesn't exist");
        }

        if(container.getName() != null && !container.getName().isBlank()) {
            existedContainer.setName(container.getName());
        }

        if(container.getType() != null && !container.getType().isBlank()) {
            existedContainer.setType(container.getType());
        }

        if(container.getLocation().getId() != null) {
            Location location = locationRepository.findById(container.getLocation().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
            existedContainer.setLocation(location);
        }

        return containerRepository.save(existedContainer);
    }

    @Transactional
    public void deleteContainer(UUID id) throws InvalidParameterException, ResourceNotFoundException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        Container container = containerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Container not found"));
        itemService.deleteItemByContainerId(id);
        containerRepository.delete(container);
    }

    @Transactional
    public void deleteContainerByLocationId(UUID id) {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found");
        }
        Iterable<Container> containerIterable = this.containerRepository.findAllByLocationId(id);
        containerIterable.forEach(container -> this.deleteContainer(container.getId()));
    }
}