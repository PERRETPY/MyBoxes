package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.repository.ContainerRepository;
import fr.perretpy.myBoxes.repository.ItemRepository;
import fr.perretpy.myBoxes.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SearchService {
    private final ContainerRepository containerRepository;
    private final ItemRepository itemRepository;
    private final SearchRepository searchRepository;


    @Autowired
    public SearchService(SearchRepository searchRepository, ContainerRepository containerRepository, ItemRepository itemRepository) {
        this.containerRepository = containerRepository;
        this.itemRepository = itemRepository;
        this.searchRepository = searchRepository;
    }

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
        Pageable customPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<Container> containerPageName = containerRepository.fuzzySearchName(query, pageable);
        Page<Container> containerPageDescription = containerRepository.fuzzySearchDescription(query, pageable);


        Page<Item> itemPageName = itemRepository.fuzzySearchName(query, pageable);
        Page<Item> itemPageDescription = itemRepository.fuzzySearchDescription(query, pageable);

        List<SearchResponseDto> results = new ArrayList<>();

        //Demande 10
        //Liste 1 : 3
        //Liste 2 : 5+(5-3) = 5+2 = 7

        //Demande 10
        //Liste 1 : 8 -> 5
        //Liste 2 : 5+(5-8)



        //results.addAll(containerPageName)
        return null;
    }


    public Page<SearchResponseDto> globalSearchV3(String query, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        // Exécuter la requête native
        List<SearchResponseDto[]> results = searchRepository.globalSearch(query, limit, offset);

        // Mapper en DTO
        List<SearchResponseDto> dtos = results.stream()
                .map(this::toDto)
                .toList();

        // Comme on utilise LIMIT/OFFSET, le total n'est pas exact.
        // Pour obtenir le total exact, il faudrait une requête COUNT séparée.
        long total = -1; // ou implementer un count séparé si besoin

        return new PageImpl<>(dtos, pageable, total);
    }

    private SearchResponseDto toDto(Object[] row) {
        SearchResponseDto dto = new SearchResponseDto();
        dto.setId(((UUID) row[0]));
        dto.setType((String) row[1]);
        dto.setName((String) row[2]);
        dto.setDescription((String) row[3]);
        dto.setScore(((Number) row[4]).doubleValue());
        return dto;
    }
}
