package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.repository.UserLocationRepository;
import com.mgs.aan.service.criteria.UserLocationCriteria;
import com.mgs.aan.service.dto.UserLocationDTO;
import com.mgs.aan.service.mapper.UserLocationMapper;
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
 * Service for executing complex queries for {@link UserLocation} entities in the database.
 * The main input is a {@link UserLocationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserLocationDTO} or a {@link Page} of {@link UserLocationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserLocationQueryService extends QueryService<UserLocation> {

    private final Logger log = LoggerFactory.getLogger(UserLocationQueryService.class);

    private final UserLocationRepository userLocationRepository;

    private final UserLocationMapper userLocationMapper;

    public UserLocationQueryService(UserLocationRepository userLocationRepository, UserLocationMapper userLocationMapper) {
        this.userLocationRepository = userLocationRepository;
        this.userLocationMapper = userLocationMapper;
    }

    /**
     * Return a {@link List} of {@link UserLocationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserLocationDTO> findByCriteria(UserLocationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserLocation> specification = createSpecification(criteria);
        return userLocationMapper.toDto(userLocationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserLocationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserLocationDTO> findByCriteria(UserLocationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserLocation> specification = createSpecification(criteria);
        return userLocationRepository.findAll(specification, page).map(userLocationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserLocationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserLocation> specification = createSpecification(criteria);
        return userLocationRepository.count(specification);
    }

    /**
     * Function to convert {@link UserLocationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserLocation> createSpecification(UserLocationCriteria criteria) {
        Specification<UserLocation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserLocation_.id));
            }
            if (criteria.getAddressLine1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine1(), UserLocation_.addressLine1));
            }
            if (criteria.getAddressLine2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine2(), UserLocation_.addressLine2));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), UserLocation_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), UserLocation_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), UserLocation_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), UserLocation_.updatedBy));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), UserLocation_.active));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(UserLocation_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getPincodesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPincodesId(),
                            root -> root.join(UserLocation_.pincodes, JoinType.LEFT).get(Pincode_.id)
                        )
                    );
            }
            if (criteria.getCountryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCountryId(),
                            root -> root.join(UserLocation_.country, JoinType.LEFT).get(Country_.id)
                        )
                    );
            }
            if (criteria.getStateId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStateId(), root -> root.join(UserLocation_.state, JoinType.LEFT).get(State_.id))
                    );
            }
            if (criteria.getDistrictId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDistrictId(),
                            root -> root.join(UserLocation_.district, JoinType.LEFT).get(District_.id)
                        )
                    );
            }
            if (criteria.getTalukaId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTalukaId(), root -> root.join(UserLocation_.taluka, JoinType.LEFT).get(Taluka_.id))
                    );
            }
            if (criteria.getVillageId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getVillageId(),
                            root -> root.join(UserLocation_.village, JoinType.LEFT).get(Village_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
