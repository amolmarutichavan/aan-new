package com.mgs.aan.repository;

import com.mgs.aan.domain.PostImage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PostImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long>, JpaSpecificationExecutor<PostImage> {}
