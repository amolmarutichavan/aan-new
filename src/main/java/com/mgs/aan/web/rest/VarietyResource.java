package com.mgs.aan.web.rest;

import com.mgs.aan.repository.VarietyRepository;
import com.mgs.aan.service.VarietyQueryService;
import com.mgs.aan.service.VarietyService;
import com.mgs.aan.service.criteria.VarietyCriteria;
import com.mgs.aan.service.dto.VarietyDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.Variety}.
 */
@RestController
@RequestMapping("/api")
public class VarietyResource {

    private final Logger log = LoggerFactory.getLogger(VarietyResource.class);

    private static final String ENTITY_NAME = "variety";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VarietyService varietyService;

    private final VarietyRepository varietyRepository;

    private final VarietyQueryService varietyQueryService;

    public VarietyResource(VarietyService varietyService, VarietyRepository varietyRepository, VarietyQueryService varietyQueryService) {
        this.varietyService = varietyService;
        this.varietyRepository = varietyRepository;
        this.varietyQueryService = varietyQueryService;
    }

    /**
     * {@code POST  /varieties} : Create a new variety.
     *
     * @param varietyDTO the varietyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new varietyDTO, or with status {@code 400 (Bad Request)} if the variety has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/varieties")
    public ResponseEntity<VarietyDTO> createVariety(@Valid @RequestBody VarietyDTO varietyDTO) throws URISyntaxException {
        log.debug("REST request to save Variety : {}", varietyDTO);
        if (varietyDTO.getId() != null) {
            throw new BadRequestAlertException("A new variety cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VarietyDTO result = varietyService.save(varietyDTO);
        return ResponseEntity
            .created(new URI("/api/varieties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /varieties/:id} : Updates an existing variety.
     *
     * @param id the id of the varietyDTO to save.
     * @param varietyDTO the varietyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated varietyDTO,
     * or with status {@code 400 (Bad Request)} if the varietyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the varietyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/varieties/{id}")
    public ResponseEntity<VarietyDTO> updateVariety(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VarietyDTO varietyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Variety : {}, {}", id, varietyDTO);
        if (varietyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, varietyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!varietyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VarietyDTO result = varietyService.update(varietyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, varietyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /varieties/:id} : Partial updates given fields of an existing variety, field will ignore if it is null
     *
     * @param id the id of the varietyDTO to save.
     * @param varietyDTO the varietyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated varietyDTO,
     * or with status {@code 400 (Bad Request)} if the varietyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the varietyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the varietyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/varieties/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VarietyDTO> partialUpdateVariety(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VarietyDTO varietyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Variety partially : {}, {}", id, varietyDTO);
        if (varietyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, varietyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!varietyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VarietyDTO> result = varietyService.partialUpdate(varietyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, varietyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /varieties} : get all the varieties.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of varieties in body.
     */
    @GetMapping("/varieties")
    public ResponseEntity<List<VarietyDTO>> getAllVarieties(VarietyCriteria criteria) {
        log.debug("REST request to get Varieties by criteria: {}", criteria);

        List<VarietyDTO> entityList = varietyQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /varieties/count} : count all the varieties.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/varieties/count")
    public ResponseEntity<Long> countVarieties(VarietyCriteria criteria) {
        log.debug("REST request to count Varieties by criteria: {}", criteria);
        return ResponseEntity.ok().body(varietyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /varieties/:id} : get the "id" variety.
     *
     * @param id the id of the varietyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the varietyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/varieties/{id}")
    public ResponseEntity<VarietyDTO> getVariety(@PathVariable Long id) {
        log.debug("REST request to get Variety : {}", id);
        Optional<VarietyDTO> varietyDTO = varietyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(varietyDTO);
    }

    @GetMapping("/varieties/product/{id}")
    public ResponseEntity<List<VarietyDTO>> getVarietyByProductId(@PathVariable("id") Long id){
        List<VarietyDTO> variety = varietyService.findByProductId(id);
        return ResponseEntity.ok().body(variety);
    }

    /**
     * {@code DELETE  /varieties/:id} : delete the "id" variety.
     *
     * @param id the id of the varietyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/varieties/{id}")
    public ResponseEntity<Void> deleteVariety(@PathVariable Long id) {
        log.debug("REST request to delete Variety : {}", id);
        varietyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
