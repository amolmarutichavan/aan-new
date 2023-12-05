package com.mgs.aan.service.impl;

import com.mgs.aan.domain.UserReview;
import com.mgs.aan.repository.UserReviewRepository;
import com.mgs.aan.service.UserReviewService;
import com.mgs.aan.service.dto.UserReviewDTO;
import com.mgs.aan.service.mapper.UserReviewMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserReview}.
 */
@Service
@Transactional
public class UserReviewServiceImpl implements UserReviewService {

    private final Logger log = LoggerFactory.getLogger(UserReviewServiceImpl.class);

    private final UserReviewRepository userReviewRepository;

    private final UserReviewMapper userReviewMapper;

    public UserReviewServiceImpl(UserReviewRepository userReviewRepository, UserReviewMapper userReviewMapper) {
        this.userReviewRepository = userReviewRepository;
        this.userReviewMapper = userReviewMapper;
    }

    @Override
    public UserReviewDTO save(UserReviewDTO userReviewDTO) {
        log.debug("Request to save UserReview : {}", userReviewDTO);
        UserReview userReview = userReviewMapper.toEntity(userReviewDTO);
        userReview = userReviewRepository.save(userReview);
        return userReviewMapper.toDto(userReview);
    }

    @Override
    public UserReviewDTO update(UserReviewDTO userReviewDTO) {
        log.debug("Request to update UserReview : {}", userReviewDTO);
        UserReview userReview = userReviewMapper.toEntity(userReviewDTO);
        userReview = userReviewRepository.save(userReview);
        return userReviewMapper.toDto(userReview);
    }

    @Override
    public Optional<UserReviewDTO> partialUpdate(UserReviewDTO userReviewDTO) {
        log.debug("Request to partially update UserReview : {}", userReviewDTO);

        return userReviewRepository
            .findById(userReviewDTO.getId())
            .map(existingUserReview -> {
                userReviewMapper.partialUpdate(existingUserReview, userReviewDTO);

                return existingUserReview;
            })
            .map(userReviewRepository::save)
            .map(userReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserReviewDTO> findAll() {
        log.debug("Request to get all UserReviews");
        return userReviewRepository.findAll().stream().map(userReviewMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserReviewDTO> findOne(Long id) {
        log.debug("Request to get UserReview : {}", id);
        return userReviewRepository.findById(id).map(userReviewMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserReview : {}", id);
        userReviewRepository.deleteById(id);
    }
}
