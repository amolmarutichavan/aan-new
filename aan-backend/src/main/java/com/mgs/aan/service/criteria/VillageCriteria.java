package com.mgs.aan.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mgs.aan.domain.Village} entity. This class is used
 * in {@link com.mgs.aan.web.rest.VillageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /villages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VillageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter updatedAt;

    private StringFilter updatedBy;

    private BooleanFilter active;

    private LongFilter userLocationId;

    private LongFilter talukaId;

    private Boolean distinct;

    public VillageCriteria() {}

    public VillageCriteria(VillageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.updatedBy = other.updatedBy == null ? null : other.updatedBy.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.userLocationId = other.userLocationId == null ? null : other.userLocationId.copy();
        this.talukaId = other.talukaId == null ? null : other.talukaId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public VillageCriteria copy() {
        return new VillageCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
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

    public LongFilter getUserLocationId() {
        return userLocationId;
    }

    public LongFilter userLocationId() {
        if (userLocationId == null) {
            userLocationId = new LongFilter();
        }
        return userLocationId;
    }

    public void setUserLocationId(LongFilter userLocationId) {
        this.userLocationId = userLocationId;
    }

    public LongFilter getTalukaId() {
        return talukaId;
    }

    public LongFilter talukaId() {
        if (talukaId == null) {
            talukaId = new LongFilter();
        }
        return talukaId;
    }

    public void setTalukaId(LongFilter talukaId) {
        this.talukaId = talukaId;
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
        final VillageCriteria that = (VillageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(active, that.active) &&
            Objects.equals(userLocationId, that.userLocationId) &&
            Objects.equals(talukaId, that.talukaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt, createdBy, updatedAt, updatedBy, active, userLocationId, talukaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VillageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            (updatedBy != null ? "updatedBy=" + updatedBy + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (userLocationId != null ? "userLocationId=" + userLocationId + ", " : "") +
            (talukaId != null ? "talukaId=" + talukaId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
