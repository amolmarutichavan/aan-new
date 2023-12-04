package com.mgs.aan.service;

import com.mgs.aan.service.dto.TalukaDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.Taluka}.
 */
public interface TalukaService {
    /**
     * Save a taluka.
     *
     * @param talukaDTO the entity to save.
     * @return the persisted entity.
     */
    TalukaDTO save(TalukaDTO talukaDTO);

    /**
     * Updates a taluka.
     *
     * @param talukaDTO the entity to update.
     * @return the persisted entity.
     */
    TalukaDTO update(TalukaDTO talukaDTO);

    /**
     * Partially updates a taluka.
     *
     * @param talukaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TalukaDTO> partialUpdate(TalukaDTO talukaDTO);

    /**
     * Get all the talukas.
     *
     * @return the list of entities.
     */
    List<TalukaDTO> findAll();

    /**
     * Get the "id" taluka.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TalukaDTO> findOne(Long id);

    /**
     * Delete the "id" taluka.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
