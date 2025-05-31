package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidPageRequestException;
import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.ContainerRepository;
import fr.perretpy.myBoxes.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Service
public class ContainerService extends GenericService<Container, UUID> {
    private final ContainerRepository containerRepository;
    private final LocationRepository locationRepository;
    private final ItemService itemService;

    @Autowired
    public ContainerService(ContainerRepository containerRepository, LocationRepository locationRepository, ItemService itemService) {
        super(containerRepository);
        this.containerRepository = containerRepository;
        this.locationRepository = locationRepository;
        this.itemService = itemService;
    }

    public Page<Container> getAllContainersByLocationId(UUID id, Pageable pageable) throws InvalidPageRequestException {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPageRequestException("Page ou taille de page invalide");
        }
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found");
        }

        return containerRepository.findAllByLocationId(id, pageable);
    }

    @Override
    public Container create(Container container) throws InvalidParameterException {
        return createWithCreater(container, (newContainer) -> {
            if(newContainer == null) {
                throw new InvalidParameterException("container cannot be null");
            }
            if(newContainer.getName().isBlank()) {
                throw new InvalidParameterException("container name cannot be null");
            }
            newContainer.setId(null);
        });
    }

    @Override
    public Container update(UUID id, Container container) throws InvalidParameterException {
        return updateWithUpdater(id, container, (existing, newEntity) -> {

            if(newEntity.getName() != null && !newEntity.getName().isBlank()) {
                existing.setName(newEntity.getName());
            }

            if(newEntity.getType() != null && !newEntity.getType().isBlank()) {
                existing.setType(newEntity.getType());
            }

            if(newEntity.getLocation().getId() != null) {
                Location location = locationRepository.findById(newEntity.getLocation().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
                existing.setLocation(location);
            }
        });
    }

    @Transactional
    @Override
    public void delete(UUID id) throws InvalidParameterException, ResourceNotFoundException {
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
        containerIterable.forEach(container -> this.delete(container.getId()));
    }
}