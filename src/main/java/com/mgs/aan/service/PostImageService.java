package com.mgs.aan.service;

import com.mgs.aan.service.dto.PostImageDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.PostImage}.
 */
public interface PostImageService {
    /**
     * Save a postImage.
     *
     * @param postImageDTO the entity to save.
     * @return the persisted entity.
     */
    PostImageDTO save(PostImageDTO postImageDTO);

    /**
     * Updates a postImage.
     *
     * @param postImageDTO the entity to update.
     * @return the persisted entity.
     */
    PostImageDTO update(PostImageDTO postImageDTO);

    /**
     * Partially updates a postImage.
     *
     * @param postImageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PostImageDTO> partialUpdate(PostImageDTO postImageDTO);

    /**
     * Get all the postImages.
     *
     * @return the list of entities.
     */
    List<PostImageDTO> findAll();

    /**
     * Get the "id" postImage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PostImageDTO> findOne(Long id);

    /**
     * Delete the "id" postImage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
