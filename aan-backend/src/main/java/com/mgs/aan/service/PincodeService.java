package com.mgs.aan.service;

import com.mgs.aan.service.dto.PincodeDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.Pincode}.
 */
public interface PincodeService {
    /**
     * Save a pincode.
     *
     * @param pincodeDTO the entity to save.
     * @return the persisted entity.
     */
    PincodeDTO save(PincodeDTO pincodeDTO);

    /**
     * Updates a pincode.
     *
     * @param pincodeDTO the entity to update.
     * @return the persisted entity.
     */
    PincodeDTO update(PincodeDTO pincodeDTO);

    /**
     * Partially updates a pincode.
     *
     * @param pincodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PincodeDTO> partialUpdate(PincodeDTO pincodeDTO);

    /**
     * Get all the pincodes.
     *
     * @return the list of entities.
     */
    List<PincodeDTO> findAll();

    /**
     * Get the "id" pincode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PincodeDTO> findOne(Long id);

    /**
     * Delete the "id" pincode.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
