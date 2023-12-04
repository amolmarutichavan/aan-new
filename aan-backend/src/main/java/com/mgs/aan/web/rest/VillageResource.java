package com.mgs.aan.web.rest;

import com.mgs.aan.repository.VillageRepository;
import com.mgs.aan.service.VillageQueryService;
import com.mgs.aan.service.VillageService;
import com.mgs.aan.service.criteria.VillageCriteria;
import com.mgs.aan.service.dto.VillageDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.Village}.
 */
@RestController
@RequestMapping("/api")
public class VillageResource {

    private final Logger log = LoggerFactory.getLogger(VillageResource.class);

    private static final String ENTITY_NAME = "village";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VillageService villageService;

    private final VillageRepository villageRepository;

    private final VillageQueryService villageQueryService;

    public VillageResource(VillageService villageService, VillageRepository villageRepository, VillageQueryService villageQueryService) {
        this.villageService = villageService;
        this.villageRepository = villageRepository;
        this.villageQueryService = villageQueryService;
    }

    /**
     * {@code POST  /villages} : Create a new village.
     *
     * @param villageDTO the villageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new villageDTO, or with status {@code 400 (Bad Request)} if the village has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/villages")
    public ResponseEntity<VillageDTO> createVillage(@Valid @RequestBody VillageDTO villageDTO) throws URISyntaxException {
        log.debug("REST request to save Village : {}", villageDTO);
        if (villageDTO.getId() != null) {
            throw new BadRequestAlertException("A new village cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VillageDTO result = villageService.save(villageDTO);
        return ResponseEntity
            .created(new URI("/api/villages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /villages/:id} : Updates an existing village.
     *
     * @param id the id of the villageDTO to save.
     * @param villageDTO the villageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated villageDTO,
     * or with status {@code 400 (Bad Request)} if the villageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the villageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/villages/{id}")
    public ResponseEntity<VillageDTO> updateVillage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VillageDTO villageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Village : {}, {}", id, villageDTO);
        if (villageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, villageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!villageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VillageDTO result = villageService.update(villageDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, villageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /villages/:id} : Partial updates given fields of an existing village, field will ignore if it is null
     *
     * @param id the id of the villageDTO to save.
     * @param villageDTO the villageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated villageDTO,
     * or with status {@code 400 (Bad Request)} if the villageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the villageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the villageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/villages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VillageDTO> partialUpdateVillage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VillageDTO villageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Village partially : {}, {}", id, villageDTO);
        if (villageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, villageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!villageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VillageDTO> result = villageService.partialUpdate(villageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, villageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /villages} : get all the villages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of villages in body.
     */
    @GetMapping("/villages")
    public ResponseEntity<List<VillageDTO>> getAllVillages(VillageCriteria criteria) {
        log.debug("REST request to get Villages by criteria: {}", criteria);

        List<VillageDTO> entityList = villageQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /villages/count} : count all the villages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/villages/count")
    public ResponseEntity<Long> countVillages(VillageCriteria criteria) {
        log.debug("REST request to count Villages by criteria: {}", criteria);
        return ResponseEntity.ok().body(villageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /villages/:id} : get the "id" village.
     *
     * @param id the id of the villageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the villageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/villages/{id}")
    public ResponseEntity<VillageDTO> getVillage(@PathVariable Long id) {
        log.debug("REST request to get Village : {}", id);
        Optional<VillageDTO> villageDTO = villageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(villageDTO);
    }

    /**
     * {@code DELETE  /villages/:id} : delete the "id" village.
     *
     * @param id the id of the villageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/villages/{id}")
    public ResponseEntity<Void> deleteVillage(@PathVariable Long id) {
        log.debug("REST request to delete Village : {}", id);
        villageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
