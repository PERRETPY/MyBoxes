package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidPageRequestException;
import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.repository.ContainerRepository;
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
    private final ItemService itemService;

    @Autowired
    public ContainerService(ContainerRepository containerRepository, ItemService itemService) {
        super(containerRepository);
        this.containerRepository = containerRepository;
        this.itemService = itemService;
    }

    public Page<Container> getAllContainersByParentId(UUID id, Pageable pageable) throws InvalidPageRequestException {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPageRequestException("Page ou taille de page invalide");
        }
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        if (!containerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Parent not found");
        }

        return containerRepository.findAllByParent_Id(id, pageable);
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

            if(newEntity.getDescription() != null && !newEntity.getDescription().isBlank()) {
                existing.setDescription(newEntity.getDescription());
            }

            if(newEntity.getParent().getId() != null) {
                Container parent = containerRepository.findById(newEntity.getParent().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
                existing.setParent(parent);
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
        this.deleteContainerByParentId(id);
        containerRepository.delete(container);
    }

    @Transactional
    public void deleteContainerByParentId(UUID id) {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        if (!containerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Parent not found");
        }
        Iterable<Container> containerIterable = this.containerRepository.findAllByParentId(id);
        containerIterable.forEach(container -> this.delete(container.getId()));
    }

    @Override
    public Page<Container> search(String query, Pageable pageable) {
        ContainerRepository containerRepository = (ContainerRepository) repository;
        Page<Container> test = containerRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
        System.out.println(test.getContent());
        return test;
    }
}