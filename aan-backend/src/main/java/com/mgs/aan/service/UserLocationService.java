package com.mgs.aan.service;

import com.mgs.aan.service.dto.UserLocationDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.UserLocation}.
 */
public interface UserLocationService {
    /**
     * Save a userLocation.
     *
     * @param userLocationDTO the entity to save.
     * @return the persisted entity.
     */
    UserLocationDTO save(UserLocationDTO userLocationDTO);

    /**
     * Updates a userLocation.
     *
     * @param userLocationDTO the entity to update.
     * @return the persisted entity.
     */
    UserLocationDTO update(UserLocationDTO userLocationDTO);

    /**
     * Partially updates a userLocation.
     *
     * @param userLocationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserLocationDTO> partialUpdate(UserLocationDTO userLocationDTO);

    /**
     * Get all the userLocations.
     *
     * @return the list of entities.
     */
    List<UserLocationDTO> findAll();

    /**
     * Get all the userLocations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserLocationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" userLocation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserLocationDTO> findOne(Long id);

    /**
     * Delete the "id" userLocation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
