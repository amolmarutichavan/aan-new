package com.mgs.aan.web.rest;

import com.mgs.aan.repository.PincodeRepository;
import com.mgs.aan.service.PincodeQueryService;
import com.mgs.aan.service.PincodeService;
import com.mgs.aan.service.criteria.PincodeCriteria;
import com.mgs.aan.service.dto.PincodeDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.Pincode}.
 */
@RestController
@RequestMapping("/api")
public class PincodeResource {

    private final Logger log = LoggerFactory.getLogger(PincodeResource.class);

    private static final String ENTITY_NAME = "pincode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PincodeService pincodeService;

    private final PincodeRepository pincodeRepository;

    private final PincodeQueryService pincodeQueryService;

    public PincodeResource(PincodeService pincodeService, PincodeRepository pincodeRepository, PincodeQueryService pincodeQueryService) {
        this.pincodeService = pincodeService;
        this.pincodeRepository = pincodeRepository;
        this.pincodeQueryService = pincodeQueryService;
    }

    /**
     * {@code POST  /pincodes} : Create a new pincode.
     *
     * @param pincodeDTO the pincodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pincodeDTO, or with status {@code 400 (Bad Request)} if the pincode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pincodes")
    public ResponseEntity<PincodeDTO> createPincode(@Valid @RequestBody PincodeDTO pincodeDTO) throws URISyntaxException {
        log.debug("REST request to save Pincode : {}", pincodeDTO);
        if (pincodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new pincode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PincodeDTO result = pincodeService.save(pincodeDTO);
        return ResponseEntity
            .created(new URI("/api/pincodes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pincodes/:id} : Updates an existing pincode.
     *
     * @param id the id of the pincodeDTO to save.
     * @param pincodeDTO the pincodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pincodeDTO,
     * or with status {@code 400 (Bad Request)} if the pincodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pincodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pincodes/{id}")
    public ResponseEntity<PincodeDTO> updatePincode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PincodeDTO pincodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Pincode : {}, {}", id, pincodeDTO);
        if (pincodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pincodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pincodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PincodeDTO result = pincodeService.update(pincodeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pincodeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pincodes/:id} : Partial updates given fields of an existing pincode, field will ignore if it is null
     *
     * @param id the id of the pincodeDTO to save.
     * @param pincodeDTO the pincodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pincodeDTO,
     * or with status {@code 400 (Bad Request)} if the pincodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pincodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pincodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pincodes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PincodeDTO> partialUpdatePincode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PincodeDTO pincodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pincode partially : {}, {}", id, pincodeDTO);
        if (pincodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pincodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pincodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PincodeDTO> result = pincodeService.partialUpdate(pincodeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pincodeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pincodes} : get all the pincodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pincodes in body.
     */
    @GetMapping("/pincodes")
    public ResponseEntity<List<PincodeDTO>> getAllPincodes(PincodeCriteria criteria) {
        log.debug("REST request to get Pincodes by criteria: {}", criteria);

        List<PincodeDTO> entityList = pincodeQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /pincodes/count} : count all the pincodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/pincodes/count")
    public ResponseEntity<Long> countPincodes(PincodeCriteria criteria) {
        log.debug("REST request to count Pincodes by criteria: {}", criteria);
        return ResponseEntity.ok().body(pincodeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /pincodes/:id} : get the "id" pincode.
     *
     * @param id the id of the pincodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pincodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pincodes/{id}")
    public ResponseEntity<PincodeDTO> getPincode(@PathVariable Long id) {
        log.debug("REST request to get Pincode : {}", id);
        Optional<PincodeDTO> pincodeDTO = pincodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pincodeDTO);
    }

    /**
     * {@code DELETE  /pincodes/:id} : delete the "id" pincode.
     *
     * @param id the id of the pincodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pincodes/{id}")
    public ResponseEntity<Void> deletePincode(@PathVariable Long id) {
        log.debug("REST request to delete Pincode : {}", id);
        pincodeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
