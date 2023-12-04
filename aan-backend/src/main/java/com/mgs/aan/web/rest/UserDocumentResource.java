package com.mgs.aan.web.rest;

import com.mgs.aan.repository.UserDocumentRepository;
import com.mgs.aan.service.UserDocumentQueryService;
import com.mgs.aan.service.UserDocumentService;
import com.mgs.aan.service.criteria.UserDocumentCriteria;
import com.mgs.aan.service.dto.UserDocumentDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.UserDocument}.
 */
@RestController
@RequestMapping("/api")
public class UserDocumentResource {

    private final Logger log = LoggerFactory.getLogger(UserDocumentResource.class);

    private static final String ENTITY_NAME = "userDocument";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserDocumentService userDocumentService;

    private final UserDocumentRepository userDocumentRepository;

    private final UserDocumentQueryService userDocumentQueryService;

    public UserDocumentResource(
        UserDocumentService userDocumentService,
        UserDocumentRepository userDocumentRepository,
        UserDocumentQueryService userDocumentQueryService
    ) {
        this.userDocumentService = userDocumentService;
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentQueryService = userDocumentQueryService;
    }

    /**
     * {@code POST  /user-documents} : Create a new userDocument.
     *
     * @param userDocumentDTO the userDocumentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userDocumentDTO, or with status {@code 400 (Bad Request)} if the userDocument has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-documents")
    public ResponseEntity<UserDocumentDTO> createUserDocument(@Valid @RequestBody UserDocumentDTO userDocumentDTO)
        throws URISyntaxException {
        log.debug("REST request to save UserDocument : {}", userDocumentDTO);
        if (userDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new userDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserDocumentDTO result = userDocumentService.save(userDocumentDTO);
        return ResponseEntity
            .created(new URI("/api/user-documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-documents/:id} : Updates an existing userDocument.
     *
     * @param id the id of the userDocumentDTO to save.
     * @param userDocumentDTO the userDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the userDocumentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-documents/{id}")
    public ResponseEntity<UserDocumentDTO> updateUserDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserDocumentDTO userDocumentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserDocument : {}, {}", id, userDocumentDTO);
        if (userDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserDocumentDTO result = userDocumentService.update(userDocumentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userDocumentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-documents/:id} : Partial updates given fields of an existing userDocument, field will ignore if it is null
     *
     * @param id the id of the userDocumentDTO to save.
     * @param userDocumentDTO the userDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the userDocumentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userDocumentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-documents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserDocumentDTO> partialUpdateUserDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserDocumentDTO userDocumentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserDocument partially : {}, {}", id, userDocumentDTO);
        if (userDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserDocumentDTO> result = userDocumentService.partialUpdate(userDocumentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userDocumentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-documents} : get all the userDocuments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userDocuments in body.
     */
    @GetMapping("/user-documents")
    public ResponseEntity<List<UserDocumentDTO>> getAllUserDocuments(UserDocumentCriteria criteria) {
        log.debug("REST request to get UserDocuments by criteria: {}", criteria);

        List<UserDocumentDTO> entityList = userDocumentQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-documents/count} : count all the userDocuments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-documents/count")
    public ResponseEntity<Long> countUserDocuments(UserDocumentCriteria criteria) {
        log.debug("REST request to count UserDocuments by criteria: {}", criteria);
        return ResponseEntity.ok().body(userDocumentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-documents/:id} : get the "id" userDocument.
     *
     * @param id the id of the userDocumentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userDocumentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-documents/{id}")
    public ResponseEntity<UserDocumentDTO> getUserDocument(@PathVariable Long id) {
        log.debug("REST request to get UserDocument : {}", id);
        Optional<UserDocumentDTO> userDocumentDTO = userDocumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userDocumentDTO);
    }

    /**
     * {@code DELETE  /user-documents/:id} : delete the "id" userDocument.
     *
     * @param id the id of the userDocumentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-documents/{id}")
    public ResponseEntity<Void> deleteUserDocument(@PathVariable Long id) {
        log.debug("REST request to delete UserDocument : {}", id);
        userDocumentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
