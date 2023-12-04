package com.mgs.aan.service;

import com.mgs.aan.domain.*; // for static metamodels
import com.mgs.aan.domain.PostImage;
import com.mgs.aan.repository.PostImageRepository;
import com.mgs.aan.service.criteria.PostImageCriteria;
import com.mgs.aan.service.dto.PostImageDTO;
import com.mgs.aan.service.mapper.PostImageMapper;
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
 * Service for executing complex queries for {@link PostImage} entities in the database.
 * The main input is a {@link PostImageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PostImageDTO} or a {@link Page} of {@link PostImageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PostImageQueryService extends QueryService<PostImage> {

    private final Logger log = LoggerFactory.getLogger(PostImageQueryService.class);

    private final PostImageRepository postImageRepository;

    private final PostImageMapper postImageMapper;

    public PostImageQueryService(PostImageRepository postImageRepository, PostImageMapper postImageMapper) {
        this.postImageRepository = postImageRepository;
        this.postImageMapper = postImageMapper;
    }

    /**
     * Return a {@link List} of {@link PostImageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PostImageDTO> findByCriteria(PostImageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PostImage> specification = createSpecification(criteria);
        return postImageMapper.toDto(postImageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PostImageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PostImageDTO> findByCriteria(PostImageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PostImage> specification = createSpecification(criteria);
        return postImageRepository.findAll(specification, page).map(postImageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PostImageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PostImage> specification = createSpecification(criteria);
        return postImageRepository.count(specification);
    }

    /**
     * Function to convert {@link PostImageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PostImage> createSpecification(PostImageCriteria criteria) {
        Specification<PostImage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PostImage_.id));
            }
            if (criteria.getImageUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImageUrl(), PostImage_.imageUrl));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), PostImage_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), PostImage_.createdBy));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), PostImage_.updatedAt));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), PostImage_.updatedBy));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), PostImage_.deleted));
            }
            if (criteria.getPostId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPostId(), root -> root.join(PostImage_.post, JoinType.LEFT).get(Post_.id))
                    );
            }
        }
        return specification;
    }
}
