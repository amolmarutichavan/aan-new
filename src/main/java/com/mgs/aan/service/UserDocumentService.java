package com.mgs.aan.service;

import com.mgs.aan.service.dto.UserDocumentDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.UserDocument}.
 */
public interface UserDocumentService {
    /**
     * Save a userDocument.
     *
     * @param userDocumentDTO the entity to save.
     * @return the persisted entity.
     */
    UserDocumentDTO save(UserDocumentDTO userDocumentDTO);

    /**
     * Updates a userDocument.
     *
     * @param userDocumentDTO the entity to update.
     * @return the persisted entity.
     */
    UserDocumentDTO update(UserDocumentDTO userDocumentDTO);

    /**
     * Partially updates a userDocument.
     *
     * @param userDocumentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserDocumentDTO> partialUpdate(UserDocumentDTO userDocumentDTO);

    /**
     * Get all the userDocuments.
     *
     * @return the list of entities.
     */
    List<UserDocumentDTO> findAll();

    /**
     * Get the "id" userDocument.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserDocumentDTO> findOne(Long id);

    /**
     * Delete the "id" userDocument.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
