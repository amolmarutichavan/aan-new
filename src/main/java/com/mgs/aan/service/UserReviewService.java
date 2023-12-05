package com.mgs.aan.service;

import com.mgs.aan.service.dto.UserReviewDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.UserReview}.
 */
public interface UserReviewService {
    /**
     * Save a userReview.
     *
     * @param userReviewDTO the entity to save.
     * @return the persisted entity.
     */
    UserReviewDTO save(UserReviewDTO userReviewDTO);

    /**
     * Updates a userReview.
     *
     * @param userReviewDTO the entity to update.
     * @return the persisted entity.
     */
    UserReviewDTO update(UserReviewDTO userReviewDTO);

    /**
     * Partially updates a userReview.
     *
     * @param userReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserReviewDTO> partialUpdate(UserReviewDTO userReviewDTO);

    /**
     * Get all the userReviews.
     *
     * @return the list of entities.
     */
    List<UserReviewDTO> findAll();

    /**
     * Get the "id" userReview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserReviewDTO> findOne(Long id);

    /**
     * Delete the "id" userReview.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
