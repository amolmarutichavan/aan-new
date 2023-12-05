package com.mgs.aan.web.rest;

import com.mgs.aan.repository.UserLocationRepository;
import com.mgs.aan.service.UserLocationQueryService;
import com.mgs.aan.service.UserLocationService;
import com.mgs.aan.service.criteria.UserLocationCriteria;
import com.mgs.aan.service.dto.UserLocationDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.UserLocation}.
 */
@RestController
@RequestMapping("/api")
public class UserLocationResource {

    private final Logger log = LoggerFactory.getLogger(UserLocationResource.class);

    private static final String ENTITY_NAME = "userLocation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserLocationService userLocationService;

    private final UserLocationRepository userLocationRepository;

    private final UserLocationQueryService userLocationQueryService;

    public UserLocationResource(
        UserLocationService userLocationService,
        UserLocationRepository userLocationRepository,
        UserLocationQueryService userLocationQueryService
    ) {
        this.userLocationService = userLocationService;
        this.userLocationRepository = userLocationRepository;
        this.userLocationQueryService = userLocationQueryService;
    }

    /**
     * {@code POST  /user-locations} : Create a new userLocation.
     *
     * @param userLocationDTO the userLocationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userLocationDTO, or with status {@code 400 (Bad Request)} if the userLocation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-locations")
    public ResponseEntity<UserLocationDTO> createUserLocation(@Valid @RequestBody UserLocationDTO userLocationDTO)
        throws URISyntaxException {
        log.debug("REST request to save UserLocation : {}", userLocationDTO);
        if (userLocationDTO.getId() != null) {
            throw new BadRequestAlertException("A new userLocation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserLocationDTO result = userLocationService.save(userLocationDTO);
        return ResponseEntity
            .created(new URI("/api/user-locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-locations/:id} : Updates an existing userLocation.
     *
     * @param id the id of the userLocationDTO to save.
     * @param userLocationDTO the userLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLocationDTO,
     * or with status {@code 400 (Bad Request)} if the userLocationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-locations/{id}")
    public ResponseEntity<UserLocationDTO> updateUserLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserLocationDTO userLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserLocation : {}, {}", id, userLocationDTO);
        if (userLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserLocationDTO result = userLocationService.update(userLocationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userLocationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-locations/:id} : Partial updates given fields of an existing userLocation, field will ignore if it is null
     *
     * @param id the id of the userLocationDTO to save.
     * @param userLocationDTO the userLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLocationDTO,
     * or with status {@code 400 (Bad Request)} if the userLocationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userLocationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-locations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserLocationDTO> partialUpdateUserLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserLocationDTO userLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserLocation partially : {}, {}", id, userLocationDTO);
        if (userLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserLocationDTO> result = userLocationService.partialUpdate(userLocationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userLocationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-locations} : get all the userLocations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userLocations in body.
     */
    @GetMapping("/user-locations")
    public ResponseEntity<List<UserLocationDTO>> getAllUserLocations(UserLocationCriteria criteria) {
        log.debug("REST request to get UserLocations by criteria: {}", criteria);

        List<UserLocationDTO> entityList = userLocationQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-locations/count} : count all the userLocations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-locations/count")
    public ResponseEntity<Long> countUserLocations(UserLocationCriteria criteria) {
        log.debug("REST request to count UserLocations by criteria: {}", criteria);
        return ResponseEntity.ok().body(userLocationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-locations/:id} : get the "id" userLocation.
     *
     * @param id the id of the userLocationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userLocationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-locations/{id}")
    public ResponseEntity<UserLocationDTO> getUserLocation(@PathVariable Long id) {
        log.debug("REST request to get UserLocation : {}", id);
        Optional<UserLocationDTO> userLocationDTO = userLocationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userLocationDTO);
    }

    /**
     * {@code DELETE  /user-locations/:id} : delete the "id" userLocation.
     *
     * @param id the id of the userLocationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-locations/{id}")
    public ResponseEntity<Void> deleteUserLocation(@PathVariable Long id) {
        log.debug("REST request to delete UserLocation : {}", id);
        userLocationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
