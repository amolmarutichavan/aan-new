package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.Variety;
import com.mgs.aan.repository.VarietyRepository;
import com.mgs.aan.service.criteria.VarietyCriteria;
import com.mgs.aan.service.dto.VarietyDTO;
import com.mgs.aan.service.mapper.VarietyMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Variety} entities in the database.
 * The main input is a {@link VarietyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VarietyDTO} or a {@link Page} of {@link VarietyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VarietyQueryService extends QueryService<Variety> {

    private final Logger log = LoggerFactory.getLogger(VarietyQueryService.class);

    private final VarietyRepository varietyRepository;

    private final VarietyMapper varietyMapper;

    public VarietyQueryService(VarietyRepository varietyRepository, VarietyMapper varietyMapper) {
        this.varietyRepository = varietyRepository;
        this.varietyMapper = varietyMapper;
    }

    /**
     * Return a {@link List} of {@link VarietyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VarietyDTO> findByCriteria(VarietyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Variety> specification = createSpecification(criteria);
        return varietyMapper.toDto(varietyRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link VarietyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VarietyDTO> findByCriteria(VarietyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Variety> specification = createSpecification(criteria);
        return varietyRepository.findAll(specification, page).map(varietyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VarietyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Variety> specification = createSpecification(criteria);
        return varietyRepository.count(specification);
    }

    /**
     * Function to convert {@link VarietyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Variety> createSpecification(VarietyCriteria criteria) {
        Specification<Variety> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Variety_.id));
            }
            if (criteria.getVarietyName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVarietyName(), Variety_.varietyName));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Variety_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Variety_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Variety_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), Variety_.updatedBy));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Variety_.deleted));
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProductId(), root -> root.join(Variety_.product, JoinType.LEFT).get(Product_.id))
                    );
            }
        }
        return specification;
    }
}
