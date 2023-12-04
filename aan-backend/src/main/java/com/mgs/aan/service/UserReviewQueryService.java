package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.UserReview;
import com.mgs.aan.repository.UserReviewRepository;
import com.mgs.aan.service.criteria.UserReviewCriteria;
import com.mgs.aan.service.dto.UserReviewDTO;
import com.mgs.aan.service.mapper.UserReviewMapper;
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
 * Service for executing complex queries for {@link UserReview} entities in the database.
 * The main input is a {@link UserReviewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserReviewDTO} or a {@link Page} of {@link UserReviewDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserReviewQueryService extends QueryService<UserReview> {

    private final Logger log = LoggerFactory.getLogger(UserReviewQueryService.class);

    private final UserReviewRepository userReviewRepository;

    private final UserReviewMapper userReviewMapper;

    public UserReviewQueryService(UserReviewRepository userReviewRepository, UserReviewMapper userReviewMapper) {
        this.userReviewRepository = userReviewRepository;
        this.userReviewMapper = userReviewMapper;
    }

    /**
     * Return a {@link List} of {@link UserReviewDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserReviewDTO> findByCriteria(UserReviewCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserReview> specification = createSpecification(criteria);
        return userReviewMapper.toDto(userReviewRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserReviewDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserReviewDTO> findByCriteria(UserReviewCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserReview> specification = createSpecification(criteria);
        return userReviewRepository.findAll(specification, page).map(userReviewMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserReviewCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserReview> specification = createSpecification(criteria);
        return userReviewRepository.count(specification);
    }

    /**
     * Function to convert {@link UserReviewCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserReview> createSpecification(UserReviewCriteria criteria) {
        Specification<UserReview> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserReview_.id));
            }
            if (criteria.getTimesTamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimesTamp(), UserReview_.timesTamp));
            }
            if (criteria.getRating() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRating(), UserReview_.rating));
            }
            if (criteria.getReview() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReview(), UserReview_.review));
            }
            if (criteria.getCategory() != null) {
                specification = specification.and(buildSpecification(criteria.getCategory(), UserReview_.category));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), UserReview_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), UserReview_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), UserReview_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), UserReview_.updatedBy));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), UserReview_.active));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(UserReview_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
