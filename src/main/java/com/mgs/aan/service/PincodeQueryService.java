package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.Pincode;
import com.mgs.aan.repository.PincodeRepository;
import com.mgs.aan.service.criteria.PincodeCriteria;
import com.mgs.aan.service.dto.PincodeDTO;
import com.mgs.aan.service.mapper.PincodeMapper;
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
 * Service for executing complex queries for {@link Pincode} entities in the database.
 * The main input is a {@link PincodeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PincodeDTO} or a {@link Page} of {@link PincodeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PincodeQueryService extends QueryService<Pincode> {

    private final Logger log = LoggerFactory.getLogger(PincodeQueryService.class);

    private final PincodeRepository pincodeRepository;

    private final PincodeMapper pincodeMapper;

    public PincodeQueryService(PincodeRepository pincodeRepository, PincodeMapper pincodeMapper) {
        this.pincodeRepository = pincodeRepository;
        this.pincodeMapper = pincodeMapper;
    }

    /**
     * Return a {@link List} of {@link PincodeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PincodeDTO> findByCriteria(PincodeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pincode> specification = createSpecification(criteria);
        return pincodeMapper.toDto(pincodeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PincodeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PincodeDTO> findByCriteria(PincodeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pincode> specification = createSpecification(criteria);
        return pincodeRepository.findAll(specification, page).map(pincodeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PincodeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pincode> specification = createSpecification(criteria);
        return pincodeRepository.count(specification);
    }

    /**
     * Function to convert {@link PincodeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pincode> createSpecification(PincodeCriteria criteria) {
        Specification<Pincode> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Pincode_.id));
            }
            if (criteria.getPinCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPinCode(), Pincode_.pinCode));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Pincode_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Pincode_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Pincode_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), Pincode_.updatedBy));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Pincode_.active));
            }
            if (criteria.getUserLocationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserLocationId(),
                            root -> root.join(Pincode_.userLocations, JoinType.LEFT).get(UserLocation_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
