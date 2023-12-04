package com.mgs.aan.repository;

import com.mgs.aan.domain.UserLocation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class UserLocationRepositoryWithBagRelationshipsImpl implements UserLocationRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserLocation> fetchBagRelationships(Optional<UserLocation> userLocation) {
        return userLocation.map(this::fetchPincodes);
    }

    @Override
    public Page<UserLocation> fetchBagRelationships(Page<UserLocation> userLocations) {
        return new PageImpl<>(
            fetchBagRelationships(userLocations.getContent()),
            userLocations.getPageable(),
            userLocations.getTotalElements()
        );
    }

    @Override
    public List<UserLocation> fetchBagRelationships(List<UserLocation> userLocations) {
        return Optional.of(userLocations).map(this::fetchPincodes).orElse(Collections.emptyList());
    }

    UserLocation fetchPincodes(UserLocation result) {
        return entityManager
            .createQuery(
                "select userLocation from UserLocation userLocation left join fetch userLocation.pincodes where userLocation.id = :id",
                UserLocation.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<UserLocation> fetchPincodes(List<UserLocation> userLocations) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, userLocations.size()).forEach(index -> order.put(userLocations.get(index).getId(), index));
        List<UserLocation> result = entityManager
            .createQuery(
                "select userLocation from UserLocation userLocation left join fetch userLocation.pincodes where userLocation in :userLocations",
                UserLocation.class
            )
            .setParameter("userLocations", userLocations)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
