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

    //TODO : doublons d'une page à l'autre
    public Page<SearchResponseDto> globalSearch(String query, Pageable pageable) {
        int page = pageable.getPageNumber();     // page index (0-based)
        int pageSize = pageable.getPageSize();   // total number of results per page
        int entityCount = 3;

        int baseQuota = pageSize / entityCount;  // minimum per entity
        int remainder = pageSize % entityCount;

        int locationQuota = baseQuota + (remainder > 0 ? 1 : 0);
        int containerQuota = baseQuota + (remainder > 1 ? 1 : 0);
        int itemQuota = baseQuota;

        int locationOffset = page * locationQuota;
        int containerOffset = page * containerQuota;
        int itemOffset = page * itemQuota;

        Pageable locationPageable = PageRequest.of(locationOffset / locationQuota, locationQuota);
        Pageable containerPageable = PageRequest.of(containerOffset / containerQuota, containerQuota);
        Pageable itemPageable = PageRequest.of(itemOffset / itemQuota, itemQuota);

        // Fetch each entity
        List<SearchResponseDto> results = new ArrayList<>();

        List<Location> locations = locationRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, locationPageable)
                .getContent();
        results.addAll(locations.stream()
                .map(SearchResponseDto::fromLocation)
                .toList());

        List<Container> containers = containerRepository
                .findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(query, query, containerPageable)
                .getContent();
        results.addAll(containers.stream()
                .map(SearchResponseDto::fromContainer)
                .toList());

        List<Item> items = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, itemPageable)
                .getContent();
        results.addAll(items.stream()
                .map(SearchResponseDto::fromItem)
                .toList());

        // If some quotas weren't fulfilled, redistribute
        int fetched = results.size();
        int toFill = pageSize - fetched;

        if (toFill > 0) {
            if (locations.size() < locationQuota) {
                int extra = Math.min(toFill, pageSize); // limit redundant fetch
                Pageable extraPageable = PageRequest.of((locationOffset + locations.size()) / extra, extra);
                List<Location> more = locationRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, extraPageable)
                        .getContent();
                List<SearchResponseDto> extraDtos = more.stream()
                        .filter(l -> results.stream().noneMatch(r -> r.getId().equals(l.getId())))
                        .map(SearchResponseDto::fromLocation)
                        .toList();
                results.addAll(extraDtos);
                toFill -= extraDtos.size();
            }

            if (toFill > 0 && containers.size() < containerQuota) {
                int extra = Math.min(toFill, pageSize);
                Pageable extraPageable = PageRequest.of((containerOffset + containers.size()) / extra, extra);
                List<Container> more = containerRepository
                        .findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(query, query, extraPageable)
                        .getContent();
                List<SearchResponseDto> extraDtos = more.stream()
                        .filter(c -> results.stream().noneMatch(r -> r.getId().equals(c.getId())))
                        .map(SearchResponseDto::fromContainer)
                        .toList();
                results.addAll(extraDtos);
                toFill -= extraDtos.size();
            }

            if (toFill > 0 && items.size() < itemQuota) {
                int extra = Math.min(toFill, pageSize);
                Pageable extraPageable = PageRequest.of((itemOffset + items.size()) / extra, extra);
                List<Item> more = itemRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, extraPageable)
                        .getContent();
                List<SearchResponseDto> extraDtos = more.stream()
                        .filter(i -> results.stream().noneMatch(r -> r.getId().equals(i.getId())))
                        .map(SearchResponseDto::fromItem)
                        .toList();
                results.addAll(extraDtos);
            }
        }

        // Enfin : paginer localement la liste construite (par sécurité)
        List<SearchResponseDto> finalPage = results.stream()
                .limit(pageSize)
                .toList();

        return new PageImpl<>(finalPage, pageable, results.size());
    }


}
