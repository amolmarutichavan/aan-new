package com.mgs.aan.repository;

import com.mgs.aan.domain.UserLocation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserLocationRepositoryWithBagRelationships {
    Optional<UserLocation> fetchBagRelationships(Optional<UserLocation> userLocation);

    List<UserLocation> fetchBagRelationships(List<UserLocation> userLocations);

    Page<UserLocation> fetchBagRelationships(Page<UserLocation> userLocations);
}
