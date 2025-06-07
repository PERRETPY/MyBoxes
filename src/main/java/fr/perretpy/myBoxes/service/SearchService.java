package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.ContainerRepository;
import fr.perretpy.myBoxes.repository.ItemRepository;
import fr.perretpy.myBoxes.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final LocationRepository locationRepository;
    private final ContainerRepository containerRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public SearchService(LocationRepository locationRepository, ContainerRepository containerRepository, ItemRepository itemRepository) {
        this.locationRepository = locationRepository;
        this.containerRepository = containerRepository;
        this.itemRepository = itemRepository;
    }

    //TODO : json result page not correct (total elements, total pages, etc.)
    public Page<SearchResponseDto> globalSearch(String query, Pageable pageable) {
        int page = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int entityCount = 3;

        int baseQuota = pageSize / entityCount;
        int remainder = pageSize % entityCount;

        int locationQuota = baseQuota + (remainder > 0 ? 1 : 0);
        int containerQuota = baseQuota + (remainder > 1 ? 1 : 0);

        long totalElement = 0;

        Pageable locationPageable = PageRequest.of(page, locationQuota);
        Pageable containerPageable = PageRequest.of(page, containerQuota);
        Pageable itemPageable = PageRequest.of(page, baseQuota);

        Page<Location> locationPage = locationRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, locationPageable);
        List<SearchResponseDto> results = new ArrayList<>(locationPage.getContent().stream()
                .map(SearchResponseDto::fromLocation)
                .toList());
        totalElement += locationPage.getTotalElements();

        Page<Container> containerPage = containerRepository
                .findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(query, query, containerPageable);
        results.addAll(containerPage.getContent().stream()
                .map(SearchResponseDto::fromContainer)
                .toList());
        totalElement += containerPage.getTotalElements();

        Page<Item> itemPage = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, itemPageable);
        results.addAll(itemPage.getContent().stream()
                .map(SearchResponseDto::fromItem)
                .toList());
        totalElement += itemPage.getTotalElements();

        System.out.println(totalElement);
        return new PageImpl<>(results, pageable, totalElement);
    }


}
