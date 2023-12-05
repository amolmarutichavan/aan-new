package com.mgs.aan.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mgs.aan.domain.UserDocument} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDocumentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String documentName;

    @NotNull
    @Size(max = 150)
    private String documentUrl;

    private Instant uploadDateTime;

    @NotNull
    @Size(max = 10)
    private String docStatus;

    private Instant createdAt;

    private String createdBy;

    private Instant updatedAt;

    private String updatedBy;

    @NotNull
    private Boolean active;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Instant getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Instant uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDocumentDTO)) {
            return false;
        }

        UserDocumentDTO userDocumentDTO = (UserDocumentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userDocumentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDocumentDTO{" +
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
            ", user=" + getUser() +
            "}";
    }
}
