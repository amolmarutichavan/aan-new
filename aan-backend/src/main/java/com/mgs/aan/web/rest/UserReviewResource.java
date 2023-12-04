package com.mgs.aan.web.rest;

import com.mgs.aan.repository.UserReviewRepository;
import com.mgs.aan.service.UserReviewQueryService;
import com.mgs.aan.service.UserReviewService;
import com.mgs.aan.service.criteria.UserReviewCriteria;
import com.mgs.aan.service.dto.UserReviewDTO;
import com.mgs.aan.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mgs.aan.domain.UserReview}.
 */
@RestController
@RequestMapping("/api")
public class UserReviewResource {

    private final Logger log = LoggerFactory.getLogger(UserReviewResource.class);

    private static final String ENTITY_NAME = "userReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserReviewService userReviewService;

    private final UserReviewRepository userReviewRepository;

    private final UserReviewQueryService userReviewQueryService;

    public UserReviewResource(
        UserReviewService userReviewService,
        UserReviewRepository userReviewRepository,
        UserReviewQueryService userReviewQueryService
    ) {
        this.userReviewService = userReviewService;
        this.userReviewRepository = userReviewRepository;
        this.userReviewQueryService = userReviewQueryService;
    }

    /**
     * {@code POST  /user-reviews} : Create a new userReview.
     *
     * @param userReviewDTO the userReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userReviewDTO, or with status {@code 400 (Bad Request)} if the userReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-reviews")
    public ResponseEntity<UserReviewDTO> createUserReview(@Valid @RequestBody UserReviewDTO userReviewDTO) throws URISyntaxException {
        log.debug("REST request to save UserReview : {}", userReviewDTO);
        if (userReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new userReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserReviewDTO result = userReviewService.save(userReviewDTO);
        return ResponseEntity
            .created(new URI("/api/user-reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-reviews/:id} : Updates an existing userReview.
     *
     * @param id the id of the userReviewDTO to save.
     * @param userReviewDTO the userReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userReviewDTO,
     * or with status {@code 400 (Bad Request)} if the userReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-reviews/{id}")
    public ResponseEntity<UserReviewDTO> updateUserReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserReviewDTO userReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserReview : {}, {}", id, userReviewDTO);
        if (userReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserReviewDTO result = userReviewService.update(userReviewDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userReviewDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-reviews/:id} : Partial updates given fields of an existing userReview, field will ignore if it is null
     *
     * @param id the id of the userReviewDTO to save.
     * @param userReviewDTO the userReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userReviewDTO,
     * or with status {@code 400 (Bad Request)} if the userReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-reviews/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserReviewDTO> partialUpdateUserReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserReviewDTO userReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserReview partially : {}, {}", id, userReviewDTO);
        if (userReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserReviewDTO> result = userReviewService.partialUpdate(userReviewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userReviewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-reviews} : get all the userReviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userReviews in body.
     */
    @GetMapping("/user-reviews")
    public ResponseEntity<List<UserReviewDTO>> getAllUserReviews(UserReviewCriteria criteria) {
        log.debug("REST request to get UserReviews by criteria: {}", criteria);

        List<UserReviewDTO> entityList = userReviewQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-reviews/count} : count all the userReviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-reviews/count")
    public ResponseEntity<Long> countUserReviews(UserReviewCriteria criteria) {
        log.debug("REST request to count UserReviews by criteria: {}", criteria);
        return ResponseEntity.ok().body(userReviewQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-reviews/:id} : get the "id" userReview.
     *
     * @param id the id of the userReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-reviews/{id}")
    public ResponseEntity<UserReviewDTO> getUserReview(@PathVariable Long id) {
        log.debug("REST request to get UserReview : {}", id);
        Optional<UserReviewDTO> userReviewDTO = userReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userReviewDTO);
    }

    /**
     * {@code DELETE  /user-reviews/:id} : delete the "id" userReview.
     *
     * @param id the id of the userReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-reviews/{id}")
    public ResponseEntity<Void> deleteUserReview(@PathVariable Long id) {
        log.debug("REST request to delete UserReview : {}", id);
        userReviewService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
