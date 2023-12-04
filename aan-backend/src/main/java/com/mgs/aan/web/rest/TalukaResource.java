package com.mgs.aan.web.rest;

import com.mgs.aan.repository.TalukaRepository;
import com.mgs.aan.service.TalukaQueryService;
import com.mgs.aan.service.TalukaService;
import com.mgs.aan.service.criteria.TalukaCriteria;
import com.mgs.aan.service.dto.TalukaDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.Taluka}.
 */
@RestController
@RequestMapping("/api")
public class TalukaResource {

    private final Logger log = LoggerFactory.getLogger(TalukaResource.class);

    private static final String ENTITY_NAME = "taluka";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TalukaService talukaService;

    private final TalukaRepository talukaRepository;

    private final TalukaQueryService talukaQueryService;

    public TalukaResource(TalukaService talukaService, TalukaRepository talukaRepository, TalukaQueryService talukaQueryService) {
        this.talukaService = talukaService;
        this.talukaRepository = talukaRepository;
        this.talukaQueryService = talukaQueryService;
    }

    /**
     * {@code POST  /talukas} : Create a new taluka.
     *
     * @param talukaDTO the talukaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new talukaDTO, or with status {@code 400 (Bad Request)} if the taluka has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/talukas")
    public ResponseEntity<TalukaDTO> createTaluka(@Valid @RequestBody TalukaDTO talukaDTO) throws URISyntaxException {
        log.debug("REST request to save Taluka : {}", talukaDTO);
        if (talukaDTO.getId() != null) {
            throw new BadRequestAlertException("A new taluka cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TalukaDTO result = talukaService.save(talukaDTO);
        return ResponseEntity
            .created(new URI("/api/talukas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /talukas/:id} : Updates an existing taluka.
     *
     * @param id the id of the talukaDTO to save.
     * @param talukaDTO the talukaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated talukaDTO,
     * or with status {@code 400 (Bad Request)} if the talukaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the talukaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/talukas/{id}")
    public ResponseEntity<TalukaDTO> updateTaluka(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TalukaDTO talukaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Taluka : {}, {}", id, talukaDTO);
        if (talukaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, talukaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!talukaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TalukaDTO result = talukaService.update(talukaDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, talukaDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /talukas/:id} : Partial updates given fields of an existing taluka, field will ignore if it is null
     *
     * @param id the id of the talukaDTO to save.
     * @param talukaDTO the talukaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated talukaDTO,
     * or with status {@code 400 (Bad Request)} if the talukaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the talukaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the talukaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/talukas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TalukaDTO> partialUpdateTaluka(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TalukaDTO talukaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Taluka partially : {}, {}", id, talukaDTO);
        if (talukaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, talukaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!talukaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TalukaDTO> result = talukaService.partialUpdate(talukaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, talukaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /talukas} : get all the talukas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of talukas in body.
     */
    @GetMapping("/talukas")
    public ResponseEntity<List<TalukaDTO>> getAllTalukas(TalukaCriteria criteria) {
        log.debug("REST request to get Talukas by criteria: {}", criteria);

        List<TalukaDTO> entityList = talukaQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /talukas/count} : count all the talukas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/talukas/count")
    public ResponseEntity<Long> countTalukas(TalukaCriteria criteria) {
        log.debug("REST request to count Talukas by criteria: {}", criteria);
        return ResponseEntity.ok().body(talukaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /talukas/:id} : get the "id" taluka.
     *
     * @param id the id of the talukaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the talukaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/talukas/{id}")
    public ResponseEntity<TalukaDTO> getTaluka(@PathVariable Long id) {
        log.debug("REST request to get Taluka : {}", id);
        Optional<TalukaDTO> talukaDTO = talukaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(talukaDTO);
    }

    /**
     * {@code DELETE  /talukas/:id} : delete the "id" taluka.
     *
     * @param id the id of the talukaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/talukas/{id}")
    public ResponseEntity<Void> deleteTaluka(@PathVariable Long id) {
        log.debug("REST request to delete Taluka : {}", id);
        talukaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
