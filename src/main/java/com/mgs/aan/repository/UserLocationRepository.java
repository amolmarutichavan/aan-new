package com.mgs.aan.repository;

import com.mgs.aan.domain.UserLocation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserLocation entity.
 *
 * When extending this class, extend UserLocationRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface UserLocationRepository
    extends UserLocationRepositoryWithBagRelationships, JpaRepository<UserLocation, Long>, JpaSpecificationExecutor<UserLocation> {
    @Query("select userLocation from UserLocation userLocation where userLocation.user.login = ?#{principal.username}")
    List<UserLocation> findByUserIsCurrentUser();

    default Optional<UserLocation> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<UserLocation> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<UserLocation> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
