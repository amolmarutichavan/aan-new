package com.mgs.aan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Pincode.
 */
@Entity
@Table(name = "pincode")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pincode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 6)
    @Column(name = "pin_code", length = 6, nullable = false)
    private String pinCode;

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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "pincodes")
    @JsonIgnoreProperties(value = { "user", "pincodes", "country", "state", "district", "taluka", "village" }, allowSetters = true)
    private Set<UserLocation> userLocations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pincode id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPinCode() {
        return this.pinCode;
    }

    public Pincode pinCode(String pinCode) {
        this.setPinCode(pinCode);
        return this;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Pincode createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Pincode createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Pincode updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Pincode updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Pincode active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<UserLocation> getUserLocations() {
        return this.userLocations;
    }

    public void setUserLocations(Set<UserLocation> userLocations) {
        if (this.userLocations != null) {
            this.userLocations.forEach(i -> i.removePincodes(this));
        }
        if (userLocations != null) {
            userLocations.forEach(i -> i.addPincodes(this));
        }
        this.userLocations = userLocations;
    }

    public Pincode userLocations(Set<UserLocation> userLocations) {
        this.setUserLocations(userLocations);
        return this;
    }

    public Pincode addUserLocation(UserLocation userLocation) {
        this.userLocations.add(userLocation);
        userLocation.getPincodes().add(this);
        return this;
    }

    public Pincode removeUserLocation(UserLocation userLocation) {
        this.userLocations.remove(userLocation);
        userLocation.getPincodes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pincode)) {
            return false;
        }
        return id != null && id.equals(((Pincode) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pincode{" +
            "id=" + getId() +
            ", pinCode='" + getPinCode() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
