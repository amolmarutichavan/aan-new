package com.mgs.aan.service.criteria;

import com.mgs.aan.domain.enumeration.Category;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mgs.aan.domain.UserReview} entity. This class is used
 * in {@link com.mgs.aan.web.rest.UserReviewResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-reviews?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserReviewCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Category
     */
    public static class CategoryFilter extends Filter<Category> {

        public CategoryFilter() {}

        public CategoryFilter(CategoryFilter filter) {
            super(filter);
        }

        @Override
        public CategoryFilter copy() {
            return new CategoryFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter timesTamp;

    private IntegerFilter rating;

    private StringFilter review;

    private CategoryFilter category;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter updatedAt;

    private StringFilter updatedBy;

    private BooleanFilter active;

    private LongFilter userId;

    private Boolean distinct;

    public UserReviewCriteria() {}

    public UserReviewCriteria(UserReviewCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timesTamp = other.timesTamp == null ? null : other.timesTamp.copy();
        this.rating = other.rating == null ? null : other.rating.copy();
        this.review = other.review == null ? null : other.review.copy();
        this.category = other.category == null ? null : other.category.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.updatedBy = other.updatedBy == null ? null : other.updatedBy.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserReviewCriteria copy() {
        return new UserReviewCriteria(this);
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

    public InstantFilter getTimesTamp() {
        return timesTamp;
    }

    public InstantFilter timesTamp() {
        if (timesTamp == null) {
            timesTamp = new InstantFilter();
        }
        return timesTamp;
    }

    public void setTimesTamp(InstantFilter timesTamp) {
        this.timesTamp = timesTamp;
    }

    public IntegerFilter getRating() {
        return rating;
    }

    public IntegerFilter rating() {
        if (rating == null) {
            rating = new IntegerFilter();
        }
        return rating;
    }

    public void setRating(IntegerFilter rating) {
        this.rating = rating;
    }

    public StringFilter getReview() {
        return review;
    }

    public StringFilter review() {
        if (review == null) {
            review = new StringFilter();
        }
        return review;
    }

    public void setReview(StringFilter review) {
        this.review = review;
    }

    public CategoryFilter getCategory() {
        return category;
    }

    public CategoryFilter category() {
        if (category == null) {
            category = new CategoryFilter();
        }
        return category;
    }

    public void setCategory(CategoryFilter category) {
        this.category = category;
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
        final UserReviewCriteria that = (UserReviewCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timesTamp, that.timesTamp) &&
            Objects.equals(rating, that.rating) &&
            Objects.equals(review, that.review) &&
            Objects.equals(category, that.category) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(active, that.active) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timesTamp, rating, review, category, createdAt, createdBy, updatedAt, updatedBy, active, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserReviewCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (timesTamp != null ? "timesTamp=" + timesTamp + ", " : "") +
            (rating != null ? "rating=" + rating + ", " : "") +
            (review != null ? "review=" + review + ", " : "") +
            (category != null ? "category=" + category + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (updatedBy != null ? "updatedBy=" + updatedBy + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
