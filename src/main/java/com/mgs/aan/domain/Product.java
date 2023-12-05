package com.mgs.aan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<Variety> varieties = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product__user",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "products")
    @JsonIgnoreProperties(value = { "postImages", "user", "products" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return this.productName;
    }

    public Product productName(String productName) {
        this.setProductName(productName);
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Product createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Product createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Product updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Product updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Product deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<Variety> getVarieties() {
        return this.varieties;
    }

    public void setVarieties(Set<Variety> varieties) {
        if (this.varieties != null) {
            this.varieties.forEach(i -> i.setProduct(null));
        }
        if (varieties != null) {
            varieties.forEach(i -> i.setProduct(this));
        }
        this.varieties = varieties;
    }

    public Product varieties(Set<Variety> varieties) {
        this.setVarieties(varieties);
        return this;
    }

    public Product addVariety(Variety variety) {
        this.varieties.add(variety);
        variety.setProduct(this);
        return this;
    }

    public Product removeVariety(Variety variety) {
        this.varieties.remove(variety);
        variety.setProduct(null);
        return this;
    }

    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Product users(Set<User> users) {
        this.setUsers(users);
        return this;
    }

    public Product addUser(User user) {
        this.users.add(user);
        return this;
    }

    public Product removeUser(User user) {
        this.users.remove(user);
        return this;
    }

    public Set<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Post> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.removeProduct(this));
        }
        if (posts != null) {
            posts.forEach(i -> i.addProduct(this));
        }
        this.posts = posts;
    }

    public Product posts(Set<Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public Product addPost(Post post) {
        this.posts.add(post);
        post.getProducts().add(this);
        return this;
    }

    public Product removePost(Post post) {
        this.posts.remove(post);
        post.getProducts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", productName='" + getProductName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
