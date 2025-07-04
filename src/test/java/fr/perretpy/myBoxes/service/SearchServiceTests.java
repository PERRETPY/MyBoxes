package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.model.Container;
import fr.perretpy.myBoxes.model.Item;
import fr.perretpy.myBoxes.model.Location;
import fr.perretpy.myBoxes.repository.ContainerRepository;
import fr.perretpy.myBoxes.repository.ItemRepository;
import fr.perretpy.myBoxes.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTests {
    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ContainerRepository containerRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    void test_globalSearch_whenValidParameter_ShouldReturnPage() {
        Location location1 = new Location(UUID.randomUUID(), "Garage", "a");
        Location location2 = new Location(UUID.randomUUID(), "Salon", "");
        Location location3 = new Location(UUID.randomUUID(), "Cuisine", "a");

        Container container1 = new Container(UUID.randomUUID(), "Boite transparente 1", "Boite", location1);
        Container container2 = new Container(UUID.randomUUID(), "Boite transparente 1", "Boite", location1);
        Container container3 = new Container(UUID.randomUUID(), "Étagère blanche", "Étagère", location1);
        Container container4 = new Container(UUID.randomUUID(), "Meuble TV", "Meuble", location3);
        Container container5 = new Container(UUID.randomUUID(), "Bout de cannapé", "Meuble", location3);

        Item item1 = new Item(UUID.randomUUID(), "Rallonge Élèctrique", "a", container1);
        Item item2 = new Item(UUID.randomUUID(), "Multiprise", "a", container1);
        Item item3 = new Item(UUID.randomUUID(), "Prise saillie", "a", container1);
        Item item4 = new Item(UUID.randomUUID(), "Ration de combat", "DLC 31/12/2026", container3);
        Item item5 = new Item(UUID.randomUUID(), "Magazine", "", container5);

        String query = "requete";
        Pageable globalPageable = PageRequest.of(0, 10);
        
        List<Location> locations = List.of(location1, location2, location3);
        Pageable locationPageable = PageRequest.of(0, 4);
        Page<Location> expectedPageLocation = new PageImpl<>(locations, locationPageable, 0);
        when(locationRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, locationPageable)).thenReturn(expectedPageLocation);

        List<Container> containers = List.of(container1, container2, container3);
        Pageable containerPageable = PageRequest.of(0, 3);
        Page<Container> expectedPageContainer = new PageImpl<>(containers, containerPageable, 0);
        when(containerRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, containerPageable)).thenReturn(expectedPageContainer);

        List<Item> items = List.of(item1, item2, item3);
        Pageable itemPageable = PageRequest.of(0, 3);
        Page<Item> expectedPageItem = new PageImpl<>(items, itemPageable, 0);
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, itemPageable)).thenReturn(expectedPageItem);

        Page<SearchResponseDto> result = searchService.globalSearch(query, globalPageable);

        assertNotNull(result);
        assertTrue(result.getContent().contains(SearchResponseDto.fromLocation(location1)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromLocation(location2)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromLocation(location3)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromContainer(container1)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromContainer(container2)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromContainer(container3)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromItem(item1)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromItem(item2)));
        assertTrue(result.getContent().contains(SearchResponseDto.fromItem(item3)));

        assertEquals(result.getSize(), globalPageable.getPageSize());
        assertEquals(result.getTotalElements(), 9);
        assertEquals(result.getNumber(), globalPageable.getPageNumber());

        verify(locationRepository, times(1)).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, locationPageable);
        verify(containerRepository, times(1)).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, containerPageable);
        verify(itemRepository, times(1)).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, itemPageable);
    }
}
