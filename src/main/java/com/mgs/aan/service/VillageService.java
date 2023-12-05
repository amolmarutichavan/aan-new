package com.mgs.aan.service;

import com.mgs.aan.service.dto.VillageDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.Village}.
 */
public interface VillageService {
    /**
     * Save a village.
     *
     * @param villageDTO the entity to save.
     * @return the persisted entity.
     */
    VillageDTO save(VillageDTO villageDTO);

    /**
     * Updates a village.
     *
     * @param villageDTO the entity to update.
     * @return the persisted entity.
     */
    VillageDTO update(VillageDTO villageDTO);

    /**
     * Partially updates a village.
     *
     * @param villageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VillageDTO> partialUpdate(VillageDTO villageDTO);

    /**
     * Get all the villages.
     *
     * @return the list of entities.
     */
    List<VillageDTO> findAll();

    /**
     * Get the "id" village.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VillageDTO> findOne(Long id);

    /**
     * Delete the "id" village.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
