package com.mgs.aan.service;

import com.mgs.aan.service.dto.VarietyDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.Variety}.
 */
public interface VarietyService {
    /**
     * Save a variety.
     *
     * @param varietyDTO the entity to save.
     * @return the persisted entity.
     */
    VarietyDTO save(VarietyDTO varietyDTO);

    /**
     * Updates a variety.
     *
     * @param varietyDTO the entity to update.
     * @return the persisted entity.
     */
    VarietyDTO update(VarietyDTO varietyDTO);

    /**
     * Partially updates a variety.
     *
     * @param varietyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VarietyDTO> partialUpdate(VarietyDTO varietyDTO);

    /**
     * Get all the varieties.
     *
     * @return the list of entities.
     */
    List<VarietyDTO> findAll();

    /**
     * Get the "id" variety.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VarietyDTO> findOne(Long id);

    /**
     * Delete the "id" variety.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
