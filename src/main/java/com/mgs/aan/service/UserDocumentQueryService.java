package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.UserDocument;
import com.mgs.aan.repository.UserDocumentRepository;
import com.mgs.aan.service.criteria.UserDocumentCriteria;
import com.mgs.aan.service.dto.UserDocumentDTO;
import com.mgs.aan.service.mapper.UserDocumentMapper;
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
 * Service for executing complex queries for {@link UserDocument} entities in the database.
 * The main input is a {@link UserDocumentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserDocumentDTO} or a {@link Page} of {@link UserDocumentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserDocumentQueryService extends QueryService<UserDocument> {

    private final Logger log = LoggerFactory.getLogger(UserDocumentQueryService.class);

    private final UserDocumentRepository userDocumentRepository;

    private final UserDocumentMapper userDocumentMapper;

    public UserDocumentQueryService(UserDocumentRepository userDocumentRepository, UserDocumentMapper userDocumentMapper) {
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentMapper = userDocumentMapper;
    }

    /**
     * Return a {@link List} of {@link UserDocumentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserDocumentDTO> findByCriteria(UserDocumentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserDocument> specification = createSpecification(criteria);
        return userDocumentMapper.toDto(userDocumentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserDocumentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserDocumentDTO> findByCriteria(UserDocumentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserDocument> specification = createSpecification(criteria);
        return userDocumentRepository.findAll(specification, page).map(userDocumentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserDocumentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserDocument> specification = createSpecification(criteria);
        return userDocumentRepository.count(specification);
    }

    /**
     * Function to convert {@link UserDocumentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserDocument> createSpecification(UserDocumentCriteria criteria) {
        Specification<UserDocument> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserDocument_.id));
            }
            if (criteria.getDocumentName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDocumentName(), UserDocument_.documentName));
            }
            if (criteria.getDocumentUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDocumentUrl(), UserDocument_.documentUrl));
            }
            if (criteria.getUploadDateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUploadDateTime(), UserDocument_.uploadDateTime));
            }
            if (criteria.getDocStatus() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDocStatus(), UserDocument_.docStatus));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), UserDocument_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), UserDocument_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), UserDocument_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), UserDocument_.updatedBy));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), UserDocument_.active));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(UserDocument_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
