package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidPageRequestException;
import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.ItemRepository;
import fr.perretpy.myBoxes.repository.ContainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Service
public class ItemService extends GenericService<Item, UUID> {
    private final ItemRepository itemRepository;
    private final ContainerRepository containerRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, ContainerRepository containerRepository) {
        super(itemRepository);
        this.itemRepository = itemRepository;
        this.containerRepository = containerRepository;
    }

    public Page<Item> getAllItemsByContainerId(UUID id, Pageable pageable) throws InvalidPageRequestException {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPageRequestException("Page ou taille de page invalide");
        }
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        if (!containerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Container not found");
        }

        return itemRepository.findAllByContainerId(id, pageable);
    }

    public Item create(Item item) throws InvalidParameterException {
        if(item == null) {
            throw new InvalidParameterException("item cannot be null");
        }
        if(item.getName().isBlank()) {
            throw new InvalidParameterException("item name cannot be null");
        }
        item.setId(null);
        return this.itemRepository.save(item);
    }

    @Override
    public Item update(UUID id, Item item) throws InvalidParameterException {
        return updateWithUpdater(id, item, (existing, newEntity) -> {
            if (newEntity.getName() != null && !newEntity.getName().isBlank()) {
                existing.setName(newEntity.getName());
            }
            if (newEntity.getDescription() != null && !newEntity.getDescription().isBlank()) {
                existing.setDescription(newEntity.getDescription());
            }
            if(newEntity.getContainer().getId() != null) {
                Container container = containerRepository.findById(item.getContainer().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Container not found"));
                existing.setContainer(container);
            }
        });
    }

    @Transactional
    @Override
    public void delete(UUID id) throws InvalidParameterException, ResourceNotFoundException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        itemRepository.delete(item);
    }

    @Transactional
    public void deleteItemByContainerId(UUID id) {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }

        if (!containerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Container not found");
        }
        Iterable<Item> itemIterable = this.itemRepository.findAllByContainerId(id);
        itemIterable.forEach(item -> this.delete(item.getId()));
    }
}