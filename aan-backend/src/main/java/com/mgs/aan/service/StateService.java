package com.mgs.aan.service;

import com.mgs.aan.service.dto.StateDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.State}.
 */
public interface StateService {
    /**
     * Save a state.
     *
     * @param stateDTO the entity to save.
     * @return the persisted entity.
     */
    StateDTO save(StateDTO stateDTO);

    /**
     * Updates a state.
     *
     * @param stateDTO the entity to update.
     * @return the persisted entity.
     */
    StateDTO update(StateDTO stateDTO);

    /**
     * Partially updates a state.
     *
     * @param stateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StateDTO> partialUpdate(StateDTO stateDTO);

    /**
     * Get all the states.
     *
     * @return the list of entities.
     */
    List<StateDTO> findAll();

    /**
     * Get the "id" state.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StateDTO> findOne(Long id);

    /**
     * Delete the "id" state.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
