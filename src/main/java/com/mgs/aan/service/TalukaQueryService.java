package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.repository.TalukaRepository;
import com.mgs.aan.service.criteria.TalukaCriteria;
import com.mgs.aan.service.dto.TalukaDTO;
import com.mgs.aan.service.mapper.TalukaMapper;
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
 * Service for executing complex queries for {@link Taluka} entities in the database.
 * The main input is a {@link TalukaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TalukaDTO} or a {@link Page} of {@link TalukaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TalukaQueryService extends QueryService<Taluka> {

    private final Logger log = LoggerFactory.getLogger(TalukaQueryService.class);

    private final TalukaRepository talukaRepository;

    private final TalukaMapper talukaMapper;

    public TalukaQueryService(TalukaRepository talukaRepository, TalukaMapper talukaMapper) {
        this.talukaRepository = talukaRepository;
        this.talukaMapper = talukaMapper;
    }

    /**
     * Return a {@link List} of {@link TalukaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TalukaDTO> findByCriteria(TalukaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Taluka> specification = createSpecification(criteria);
        return talukaMapper.toDto(talukaRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TalukaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TalukaDTO> findByCriteria(TalukaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Taluka> specification = createSpecification(criteria);
        return talukaRepository.findAll(specification, page).map(talukaMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TalukaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Taluka> specification = createSpecification(criteria);
        return talukaRepository.count(specification);
    }

    /**
     * Function to convert {@link TalukaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Taluka> createSpecification(TalukaCriteria criteria) {
        Specification<Taluka> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Taluka_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Taluka_.name));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Taluka_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Taluka_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Taluka_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), Taluka_.updatedBy));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Taluka_.active));
            }
            if (criteria.getVillageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVillageId(), root -> root.join(Taluka_.villages, JoinType.LEFT).get(Village_.id))
                    );
            }
            if (criteria.getUserLocationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserLocationId(),
                            root -> root.join(Taluka_.userLocations, JoinType.LEFT).get(UserLocation_.id)
                        )
                    );
            }
            if (criteria.getDistrictId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDistrictId(), root -> root.join(Taluka_.district, JoinType.LEFT).get(District_.id))
                    );
            }
        }
        return specification;
    }
}
