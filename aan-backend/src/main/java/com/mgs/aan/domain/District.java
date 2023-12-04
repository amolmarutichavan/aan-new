package com.mgs.aan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A District.
 */
@Entity
@Table(name = "district")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class District implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    @JsonIgnoreProperties(value = { "villages", "userLocations", "district" }, allowSetters = true)
    private Set<Taluka> talukas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    @JsonIgnoreProperties(value = { "user", "pincodes", "country", "state", "district", "taluka", "village" }, allowSetters = true)
    private Set<UserLocation> userLocations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "districts", "userLocations", "country" }, allowSetters = true)
    private State state;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public District id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public District name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public District createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public District createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public District updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public District updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return this.active;
    }

    public District active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Taluka> getTalukas() {
        return this.talukas;
    }

    public void setTalukas(Set<Taluka> talukas) {
        if (this.talukas != null) {
            this.talukas.forEach(i -> i.setDistrict(null));
        }
        if (talukas != null) {
            talukas.forEach(i -> i.setDistrict(this));
        }
        this.talukas = talukas;
    }

    public District talukas(Set<Taluka> talukas) {
        this.setTalukas(talukas);
        return this;
    }

    public District addTaluka(Taluka taluka) {
        this.talukas.add(taluka);
        taluka.setDistrict(this);
        return this;
    }

    public District removeTaluka(Taluka taluka) {
        this.talukas.remove(taluka);
        taluka.setDistrict(null);
        return this;
    }

    public Set<UserLocation> getUserLocations() {
        return this.userLocations;
    }

    public void setUserLocations(Set<UserLocation> userLocations) {
        if (this.userLocations != null) {
            this.userLocations.forEach(i -> i.setDistrict(null));
        }
        if (userLocations != null) {
            userLocations.forEach(i -> i.setDistrict(this));
        }
        this.userLocations = userLocations;
    }

    public District userLocations(Set<UserLocation> userLocations) {
        this.setUserLocations(userLocations);
        return this;
    }

    public District addUserLocation(UserLocation userLocation) {
        this.userLocations.add(userLocation);
        userLocation.setDistrict(this);
        return this;
    }

    public District removeUserLocation(UserLocation userLocation) {
        this.userLocations.remove(userLocation);
        userLocation.setDistrict(null);
        return this;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public District state(State state) {
        this.setState(state);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof District)) {
            return false;
        }
        return id != null && id.equals(((District) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "District{" +
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
