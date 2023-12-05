package com.mgs.aan.service;

import com.mgs.aan.service.dto.CategoryDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mgs.aan.domain.Category}.
 */
public interface CategoryService {
    /**
     * Save a category.
     *
     * @param categoryDTO the entity to save.
     * @return the persisted entity.
     */
    CategoryDTO save(CategoryDTO categoryDTO);

    /**
     * Updates a category.
     *
     * @param categoryDTO the entity to update.
     * @return the persisted entity.
     */
    CategoryDTO update(CategoryDTO categoryDTO);

    /**
     * Partially updates a category.
     *
     * @param categoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CategoryDTO> partialUpdate(CategoryDTO categoryDTO);

    /**
     * Get all the categories.
     *
     * @return the list of entities.
     */
    List<CategoryDTO> findAll();

    /**
     * Get all the CategoryDTO where Product is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<CategoryDTO> findAllWhereProductIsNull();

    /**
     * Get the "id" category.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CategoryDTO> findOne(Long id);

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
