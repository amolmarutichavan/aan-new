package com.mgs.aan.service.criteria;

import com.mgs.aan.domain.enumeration.PostType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mgs.aan.domain.Post} entity. This class is used
 * in {@link com.mgs.aan.web.rest.PostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PostType
     */
    public static class PostTypeFilter extends Filter<PostType> {

        public PostTypeFilter() {}

        public PostTypeFilter(PostTypeFilter filter) {
            super(filter);
        }

        @Override
        public PostTypeFilter copy() {
            return new PostTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private PostTypeFilter postType;

    private LongFilter quantity;

    private DoubleFilter unit;

    private BigDecimalFilter minPrice;

    private BigDecimalFilter maxPrice;

    private LocalDateFilter targetDate;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter updatedAt;

    private StringFilter updatedBy;

    private BooleanFilter active;

    private LongFilter postImageId;

    private LongFilter userId;

    private LongFilter productId;

    private Boolean distinct;

    public PostCriteria() {}

    public PostCriteria(PostCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.postType = other.postType == null ? null : other.postType.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.unit = other.unit == null ? null : other.unit.copy();
        this.minPrice = other.minPrice == null ? null : other.minPrice.copy();
        this.maxPrice = other.maxPrice == null ? null : other.maxPrice.copy();
        this.targetDate = other.targetDate == null ? null : other.targetDate.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.updatedBy = other.updatedBy == null ? null : other.updatedBy.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.postImageId = other.postImageId == null ? null : other.postImageId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PostCriteria copy() {
        return new PostCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public PostTypeFilter getPostType() {
        return postType;
    }

    public PostTypeFilter postType() {
        if (postType == null) {
            postType = new PostTypeFilter();
        }
        return postType;
    }

    public void setPostType(PostTypeFilter postType) {
        this.postType = postType;
    }

    public LongFilter getQuantity() {
        return quantity;
    }

    public LongFilter quantity() {
        if (quantity == null) {
            quantity = new LongFilter();
        }
        return quantity;
    }

    public void setQuantity(LongFilter quantity) {
        this.quantity = quantity;
    }

    public DoubleFilter getUnit() {
        return unit;
    }

    public DoubleFilter unit() {
        if (unit == null) {
            unit = new DoubleFilter();
        }
        return unit;
    }

    public void setUnit(DoubleFilter unit) {
        this.unit = unit;
    }

    public BigDecimalFilter getMinPrice() {
        return minPrice;
    }

    public BigDecimalFilter minPrice() {
        if (minPrice == null) {
            minPrice = new BigDecimalFilter();
        }
        return minPrice;
    }

    public void setMinPrice(BigDecimalFilter minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimalFilter getMaxPrice() {
        return maxPrice;
    }

    public BigDecimalFilter maxPrice() {
        if (maxPrice == null) {
            maxPrice = new BigDecimalFilter();
        }
        return maxPrice;
    }

    public void setMaxPrice(BigDecimalFilter maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDateFilter getTargetDate() {
        return targetDate;
    }

    public LocalDateFilter targetDate() {
        if (targetDate == null) {
            targetDate = new LocalDateFilter();
        }
        return targetDate;
    }

    public void setTargetDate(LocalDateFilter targetDate) {
        this.targetDate = targetDate;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            createdAt = new InstantFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            updatedAt = new InstantFilter();
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StringFilter getUpdatedBy() {
        return updatedBy;
    }

    public StringFilter updatedBy() {
        if (updatedBy == null) {
            updatedBy = new StringFilter();
        }
        return updatedBy;
    }

    public void setUpdatedBy(StringFilter updatedBy) {
        this.updatedBy = updatedBy;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public LongFilter getPostImageId() {
        return postImageId;
    }

    public LongFilter postImageId() {
        if (postImageId == null) {
            postImageId = new LongFilter();
        }
        return postImageId;
    }

    public void setPostImageId(LongFilter postImageId) {
        this.postImageId = postImageId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PostCriteria that = (PostCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(postType, that.postType) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(minPrice, that.minPrice) &&
            Objects.equals(maxPrice, that.maxPrice) &&
            Objects.equals(targetDate, that.targetDate) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(active, that.active) &&
            Objects.equals(postImageId, that.postImageId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            postType,
            quantity,
            unit,
            minPrice,
            maxPrice,
            targetDate,
            createdAt,
            createdBy,
            updatedAt,
            updatedBy,
            active,
            postImageId,
            userId,
            productId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (postType != null ? "postType=" + postType + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (unit != null ? "unit=" + unit + ", " : "") +
            (minPrice != null ? "minPrice=" + minPrice + ", " : "") +
            (maxPrice != null ? "maxPrice=" + maxPrice + ", " : "") +
            (targetDate != null ? "targetDate=" + targetDate + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (updatedBy != null ? "updatedBy=" + updatedBy + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (postImageId != null ? "postImageId=" + postImageId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
