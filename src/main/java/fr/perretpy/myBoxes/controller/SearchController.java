package fr.perretpy.myBoxes.controller;

import fr.perretpy.myBoxes.dto.SearchResponseDto;
import fr.perretpy.myBoxes.service.SearchService;
import fr.perretpy.myBoxes.util.HateoasUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/search")
@PreAuthorize("hasAuthority('ROLE_myboxes_admin')")
public class SearchController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchService searchService;
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SearchResponseDto>>> search(@RequestParam("q") String query, Pageable pageable) {
        logger.info("GET request on /api/search with query \" {} \" and parameters: {}", query, pageable);

        Page<SearchResponseDto> searchResponseDtoPage = searchService.globalSearch(query, pageable);

        Link selfLink = linkTo(methodOn(SearchController.class).search(query, pageable)).withSelfRel();

        /*return ResponseEntity.ok(HateoasUtil.toPagedModel(
                searchResponseDtoPage,
                pageable,
                HateoasUtil::toEntityModel,
                selfLink
        ));*/
        return null;
    }
}
