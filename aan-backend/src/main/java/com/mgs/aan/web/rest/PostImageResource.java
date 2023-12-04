package com.mgs.aan.web.rest;

import com.mgs.aan.repository.PostImageRepository;
import com.mgs.aan.service.PostImageQueryService;
import com.mgs.aan.service.PostImageService;
import com.mgs.aan.service.criteria.PostImageCriteria;
import com.mgs.aan.service.dto.PostImageDTO;
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
 * REST controller for managing {@link com.mgs.aan.domain.PostImage}.
 */
@RestController
@RequestMapping("/api")
public class PostImageResource {

    private final Logger log = LoggerFactory.getLogger(PostImageResource.class);

    private static final String ENTITY_NAME = "postImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostImageService postImageService;

    private final PostImageRepository postImageRepository;

    private final PostImageQueryService postImageQueryService;

    public PostImageResource(
        PostImageService postImageService,
        PostImageRepository postImageRepository,
        PostImageQueryService postImageQueryService
    ) {
        this.postImageService = postImageService;
        this.postImageRepository = postImageRepository;
        this.postImageQueryService = postImageQueryService;
    }

    /**
     * {@code POST  /post-images} : Create a new postImage.
     *
     * @param postImageDTO the postImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postImageDTO, or with status {@code 400 (Bad Request)} if the postImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/post-images")
    public ResponseEntity<PostImageDTO> createPostImage(@Valid @RequestBody PostImageDTO postImageDTO) throws URISyntaxException {
        log.debug("REST request to save PostImage : {}", postImageDTO);
        if (postImageDTO.getId() != null) {
            throw new BadRequestAlertException("A new postImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PostImageDTO result = postImageService.save(postImageDTO);
        return ResponseEntity
            .created(new URI("/api/post-images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /post-images/:id} : Updates an existing postImage.
     *
     * @param id the id of the postImageDTO to save.
     * @param postImageDTO the postImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postImageDTO,
     * or with status {@code 400 (Bad Request)} if the postImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/post-images/{id}")
    public ResponseEntity<PostImageDTO> updatePostImage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostImageDTO postImageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PostImage : {}, {}", id, postImageDTO);
        if (postImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PostImageDTO result = postImageService.update(postImageDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, postImageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /post-images/:id} : Partial updates given fields of an existing postImage, field will ignore if it is null
     *
     * @param id the id of the postImageDTO to save.
     * @param postImageDTO the postImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postImageDTO,
     * or with status {@code 400 (Bad Request)} if the postImageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postImageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/post-images/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostImageDTO> partialUpdatePostImage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostImageDTO postImageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PostImage partially : {}, {}", id, postImageDTO);
        if (postImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostImageDTO> result = postImageService.partialUpdate(postImageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, postImageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /post-images} : get all the postImages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postImages in body.
     */
    @GetMapping("/post-images")
    public ResponseEntity<List<PostImageDTO>> getAllPostImages(PostImageCriteria criteria) {
        log.debug("REST request to get PostImages by criteria: {}", criteria);

        List<PostImageDTO> entityList = postImageQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /post-images/count} : count all the postImages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/post-images/count")
    public ResponseEntity<Long> countPostImages(PostImageCriteria criteria) {
        log.debug("REST request to count PostImages by criteria: {}", criteria);
        return ResponseEntity.ok().body(postImageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /post-images/:id} : get the "id" postImage.
     *
     * @param id the id of the postImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/post-images/{id}")
    public ResponseEntity<PostImageDTO> getPostImage(@PathVariable Long id) {
        log.debug("REST request to get PostImage : {}", id);
        Optional<PostImageDTO> postImageDTO = postImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postImageDTO);
    }

    /**
     * {@code DELETE  /post-images/:id} : delete the "id" postImage.
     *
     * @param id the id of the postImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/post-images/{id}")
    public ResponseEntity<Void> deletePostImage(@PathVariable Long id) {
        log.debug("REST request to delete PostImage : {}", id);
        postImageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
