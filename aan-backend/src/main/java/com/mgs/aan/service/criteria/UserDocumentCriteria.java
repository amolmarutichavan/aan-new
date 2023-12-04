package com.mgs.aan.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mgs.aan.domain.UserDocument} entity. This class is used
 * in {@link com.mgs.aan.web.rest.UserDocumentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-documents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDocumentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter documentName;

    private StringFilter documentUrl;

    private InstantFilter uploadDateTime;

    private StringFilter docStatus;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter updatedAt;

    private StringFilter updatedBy;

    private BooleanFilter active;

    private LongFilter userId;

    private Boolean distinct;

    public UserDocumentCriteria() {}

    public UserDocumentCriteria(UserDocumentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.documentName = other.documentName == null ? null : other.documentName.copy();
        this.documentUrl = other.documentUrl == null ? null : other.documentUrl.copy();
        this.uploadDateTime = other.uploadDateTime == null ? null : other.uploadDateTime.copy();
        this.docStatus = other.docStatus == null ? null : other.docStatus.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
        this.updatedBy = other.updatedBy == null ? null : other.updatedBy.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserDocumentCriteria copy() {
        return new UserDocumentCriteria(this);
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

    public StringFilter getDocumentName() {
        return documentName;
    }

    public StringFilter documentName() {
        if (documentName == null) {
            documentName = new StringFilter();
        }
        return documentName;
    }

    public void setDocumentName(StringFilter documentName) {
        this.documentName = documentName;
    }

    public StringFilter getDocumentUrl() {
        return documentUrl;
    }

    public StringFilter documentUrl() {
        if (documentUrl == null) {
            documentUrl = new StringFilter();
        }
        return documentUrl;
    }

    public void setDocumentUrl(StringFilter documentUrl) {
        this.documentUrl = documentUrl;
    }

    public InstantFilter getUploadDateTime() {
        return uploadDateTime;
    }

    public InstantFilter uploadDateTime() {
        if (uploadDateTime == null) {
            uploadDateTime = new InstantFilter();
        }
        return uploadDateTime;
    }

    public void setUploadDateTime(InstantFilter uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public StringFilter getDocStatus() {
        return docStatus;
    }

    public StringFilter docStatus() {
        if (docStatus == null) {
            docStatus = new StringFilter();
        }
        return docStatus;
    }

    public void setDocStatus(StringFilter docStatus) {
        this.docStatus = docStatus;
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
        final UserDocumentCriteria that = (UserDocumentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(documentName, that.documentName) &&
            Objects.equals(documentUrl, that.documentUrl) &&
            Objects.equals(uploadDateTime, that.uploadDateTime) &&
            Objects.equals(docStatus, that.docStatus) &&
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
        return Objects.hash(
            id,
            documentName,
            documentUrl,
            uploadDateTime,
            docStatus,
            createdAt,
            createdBy,
            updatedAt,
            updatedBy,
            active,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDocumentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (documentName != null ? "documentName=" + documentName + ", " : "") +
            (documentUrl != null ? "documentUrl=" + documentUrl + ", " : "") +
            (uploadDateTime != null ? "uploadDateTime=" + uploadDateTime + ", " : "") +
            (docStatus != null ? "docStatus=" + docStatus + ", " : "") +
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
