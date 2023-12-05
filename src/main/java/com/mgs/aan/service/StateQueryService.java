package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.State;
import com.mgs.aan.repository.StateRepository;
import com.mgs.aan.service.criteria.StateCriteria;
import com.mgs.aan.service.dto.StateDTO;
import com.mgs.aan.service.mapper.StateMapper;
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
 * Service for executing complex queries for {@link State} entities in the database.
 * The main input is a {@link StateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StateDTO} or a {@link Page} of {@link StateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StateQueryService extends QueryService<State> {

    private final Logger log = LoggerFactory.getLogger(StateQueryService.class);

    private final StateRepository stateRepository;

    private final StateMapper stateMapper;

    public StateQueryService(StateRepository stateRepository, StateMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    /**
     * Return a {@link List} of {@link StateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StateDTO> findByCriteria(StateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<State> specification = createSpecification(criteria);
        return stateMapper.toDto(stateRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StateDTO> findByCriteria(StateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<State> specification = createSpecification(criteria);
        return stateRepository.findAll(specification, page).map(stateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<State> specification = createSpecification(criteria);
        return stateRepository.count(specification);
    }

    /**
     * Function to convert {@link StateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<State> createSpecification(StateCriteria criteria) {
        Specification<State> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), State_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), State_.name));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), State_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), State_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), State_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), State_.updatedBy));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), State_.active));
            }
            if (criteria.getDistrictId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDistrictId(), root -> root.join(State_.districts, JoinType.LEFT).get(District_.id))
                    );
            }
            if (criteria.getUserLocationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserLocationId(),
                            root -> root.join(State_.userLocations, JoinType.LEFT).get(UserLocation_.id)
                        )
                    );
            }
            if (criteria.getCountryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCountryId(), root -> root.join(State_.country, JoinType.LEFT).get(Country_.id))
                    );
            }
        }
        return specification;
    }
}
