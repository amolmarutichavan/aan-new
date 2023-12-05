package com.mgs.aan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Taluka.
 */
@Entity
@Table(name = "taluka")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Taluka implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taluka")
    @JsonIgnoreProperties(value = { "userLocations", "taluka" }, allowSetters = true)
    private Set<Village> villages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taluka")
    @JsonIgnoreProperties(value = { "user", "pincodes", "country", "state", "district", "taluka", "village" }, allowSetters = true)
    private Set<UserLocation> userLocations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "talukas", "userLocations", "state" }, allowSetters = true)
    private District district;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Taluka id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Taluka name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Taluka createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Taluka createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Taluka updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Taluka updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Taluka active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Village> getVillages() {
        return this.villages;
    }

    public void setVillages(Set<Village> villages) {
        if (this.villages != null) {
            this.villages.forEach(i -> i.setTaluka(null));
        }
        if (villages != null) {
            villages.forEach(i -> i.setTaluka(this));
        }
        this.villages = villages;
    }

    public Taluka villages(Set<Village> villages) {
        this.setVillages(villages);
        return this;
    }

    public Taluka addVillage(Village village) {
        this.villages.add(village);
        village.setTaluka(this);
        return this;
    }

    public Taluka removeVillage(Village village) {
        this.villages.remove(village);
        village.setTaluka(null);
        return this;
    }

    public Set<UserLocation> getUserLocations() {
        return this.userLocations;
    }

    public void setUserLocations(Set<UserLocation> userLocations) {
        if (this.userLocations != null) {
            this.userLocations.forEach(i -> i.setTaluka(null));
        }
        if (userLocations != null) {
            userLocations.forEach(i -> i.setTaluka(this));
        }
        this.userLocations = userLocations;
    }

    public Taluka userLocations(Set<UserLocation> userLocations) {
        this.setUserLocations(userLocations);
        return this;
    }

    public Taluka addUserLocation(UserLocation userLocation) {
        this.userLocations.add(userLocation);
        userLocation.setTaluka(this);
        return this;
    }

    public Taluka removeUserLocation(UserLocation userLocation) {
        this.userLocations.remove(userLocation);
        userLocation.setTaluka(null);
        return this;
    }

    public District getDistrict() {
        return this.district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Taluka district(District district) {
        this.setDistrict(district);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Taluka)) {
            return false;
        }
        return id != null && id.equals(((Taluka) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Taluka{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
