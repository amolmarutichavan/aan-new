package com.mgs.aan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A UserDocument.
 */
@Entity
@Table(name = "user_document")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column(name = "document_name", length = 150, nullable = false)
    private String documentName;

    @NotNull
    @Size(max = 150)
    @Column(name = "document_url", length = 150, nullable = false)
    private String documentUrl;

    @Column(name = "upload_date_time")
    private Instant uploadDateTime;

    @NotNull
    @Size(max = 10)
    @Column(name = "doc_status", length = 10, nullable = false)
    private String docStatus;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserDocument id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public UserDocument documentName(String documentName) {
        this.setDocumentName(documentName);
        return this;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentUrl() {
        return this.documentUrl;
    }

    public UserDocument documentUrl(String documentUrl) {
        this.setDocumentUrl(documentUrl);
        return this;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Instant getUploadDateTime() {
        return this.uploadDateTime;
    }

    public UserDocument uploadDateTime(Instant uploadDateTime) {
        this.setUploadDateTime(uploadDateTime);
        return this;
    }

    public void setUploadDateTime(Instant uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getDocStatus() {
        return this.docStatus;
    }

    public UserDocument docStatus(String docStatus) {
        this.setDocStatus(docStatus);
        return this;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public UserDocument createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public UserDocument createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public UserDocument updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public UserDocument updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return this.active;
    }

    public UserDocument active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserDocument user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDocument)) {
            return false;
        }
        return id != null && id.equals(((UserDocument) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDocument{" +
            "id=" + getId() +
            ", documentName='" + getDocumentName() + "'" +
            ", documentUrl='" + getDocumentUrl() + "'" +
            ", uploadDateTime='" + getUploadDateTime() + "'" +
            ", docStatus='" + getDocStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
