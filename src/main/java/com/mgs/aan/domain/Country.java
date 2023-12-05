package com.mgs.aan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Country.
 */
@Entity
@Table(name = "country")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Country implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    @JsonIgnoreProperties(value = { "districts", "userLocations", "country" }, allowSetters = true)
    private Set<State> states = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    @JsonIgnoreProperties(value = { "user", "pincodes", "country", "state", "district", "taluka", "village" }, allowSetters = true)
    private Set<UserLocation> userLocations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Country id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Country name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Country createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Country createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Country updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Country updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Country active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<State> getStates() {
        return this.states;
    }

    public void setStates(Set<State> states) {
        if (this.states != null) {
            this.states.forEach(i -> i.setCountry(null));
        }
        if (states != null) {
            states.forEach(i -> i.setCountry(this));
        }
        this.states = states;
    }

    public Country states(Set<State> states) {
        this.setStates(states);
        return this;
    }

    public Country addStates(State state) {
        this.states.add(state);
        state.setCountry(this);
        return this;
    }

    public Country removeStates(State state) {
        this.states.remove(state);
        state.setCountry(null);
        return this;
    }

    public Set<UserLocation> getUserLocations() {
        return this.userLocations;
    }

    public void setUserLocations(Set<UserLocation> userLocations) {
        if (this.userLocations != null) {
            this.userLocations.forEach(i -> i.setCountry(null));
        }
        if (userLocations != null) {
            userLocations.forEach(i -> i.setCountry(this));
        }
        this.userLocations = userLocations;
    }

    public Country userLocations(Set<UserLocation> userLocations) {
        this.setUserLocations(userLocations);
        return this;
    }

    public Country addUserLocation(UserLocation userLocation) {
        this.userLocations.add(userLocation);
        userLocation.setCountry(this);
        return this;
    }

    public Country removeUserLocation(UserLocation userLocation) {
        this.userLocations.remove(userLocation);
        userLocation.setCountry(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return id != null && id.equals(((Country) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
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
