package com.mgs.aan.repository;

import com.mgs.aan.domain.Variety;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Variety entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VarietyRepository extends JpaRepository<Variety, Long>, JpaSpecificationExecutor<Variety> {
    List<Variety> findAllByProductId(Long id);
}
