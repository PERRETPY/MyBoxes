package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.repository.ContainerRepository;
import fr.perretpy.myBoxes.repository.ItemRepository;
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
    private final ContainerRepository containerRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public SearchService(ContainerRepository containerRepository, ItemRepository itemRepository) {
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

        int containerQuota = baseQuota + (remainder > 1 ? 1 : 0);

        long totalElement = 0;

        Pageable containerPageable = PageRequest.of(page, containerQuota);
        Pageable itemPageable = PageRequest.of(page, baseQuota);

        Page<Container> containerPage = containerRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, containerPageable);
        List<SearchResponseDto> results = new ArrayList<>(containerPage.getContent().stream()
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

    public Page<SearchResponseDto> globalSearchV2(String query, Pageable pageable) {
        Page<Container> containerPageName = containerRepository.fuzzySearchName(query, pageable);
        Page<Container> containerPageDescription = containerRepository.fuzzySearchDescription(query, pageable);

        /*
        Page<Item> itemPageName = itemRepository.fuzzySearchName(query);
        Page<Item> itemPageDescription = itemRepository.fuzzySearchDescription(query);
         */

        List<SearchResponseDto> results = new ArrayList<>();

        //results.addAll(containerPageName)
        return null;
    }
}
