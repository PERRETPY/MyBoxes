package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidPageRequestException;
import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.repository.ItemRepository;
import fr.perretpy.myBoxes.repository.ContainerRepository;
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
public class ItemService {
    private ItemRepository itemRepository;
    private ContainerRepository containerRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, ContainerRepository containerRepository) {
        this.itemRepository = itemRepository;
        this.containerRepository = containerRepository;
    }

    public Page<Item> getAllItems(Pageable pageable) throws InvalidPageRequestException {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPageRequestException("Page ou taille de page invalide");
        }

        return itemRepository.findAll(pageable);
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

    public Optional<Item> getItem(UUID id) throws InvalidParameterException {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }
        return this.itemRepository.findById(id);
    }

    public Item createItem(Item item) throws InvalidParameterException {
        if(item == null) {
            throw new InvalidParameterException("item cannot be null");
        }
        if(item.getName().isBlank()) {
            throw new InvalidParameterException("item name cannot be null");
        }
        item.setId(null);
        return this.itemRepository.save(item);
    }

    public Item updateItem(UUID id, Item item) throws InvalidParameterException {
        Optional<Item> optionalExistedItem = getItem(id);
        Item existedItem;
        if (optionalExistedItem.isPresent()) {
            existedItem = optionalExistedItem.get();
        }else {
            throw new InvalidParameterException("room id doesn't exist");
        }

        if(item.getName() != null && !item.getName().isBlank()) {
            existedItem.setName(item.getName());
        }

        if(item.getDescription() != null && !item.getDescription().isBlank()) {
            existedItem.setDescription(item.getDescription());
        }

        if(item.getContainer().getId() != null) {
            Container container = containerRepository.findById(item.getContainer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Container not found"));
            existedItem.setContainer(container);
        }

        return itemRepository.save(existedItem);
    }

    @Transactional
    public void deleteItem(UUID id) throws InvalidParameterException, ResourceNotFoundException {
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
        itemIterable.forEach(item -> this.deleteItem(item.getId()));
    }
}