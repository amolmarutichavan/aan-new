package com.mgs.aan.service.impl;

import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.repository.UserLocationRepository;
import com.mgs.aan.service.UserLocationService;
import com.mgs.aan.service.dto.UserLocationDTO;
import com.mgs.aan.service.mapper.UserLocationMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserLocation}.
 */
@Service
@Transactional
public class UserLocationServiceImpl implements UserLocationService {

    private final Logger log = LoggerFactory.getLogger(UserLocationServiceImpl.class);

    private final UserLocationRepository userLocationRepository;

    private final UserLocationMapper userLocationMapper;

    public UserLocationServiceImpl(UserLocationRepository userLocationRepository, UserLocationMapper userLocationMapper) {
        this.userLocationRepository = userLocationRepository;
        this.userLocationMapper = userLocationMapper;
    }

    @Override
    public UserLocationDTO save(UserLocationDTO userLocationDTO) {
        log.debug("Request to save UserLocation : {}", userLocationDTO);
        UserLocation userLocation = userLocationMapper.toEntity(userLocationDTO);
        userLocation = userLocationRepository.save(userLocation);
        return userLocationMapper.toDto(userLocation);
    }

    @Override
    public UserLocationDTO update(UserLocationDTO userLocationDTO) {
        log.debug("Request to update UserLocation : {}", userLocationDTO);
        UserLocation userLocation = userLocationMapper.toEntity(userLocationDTO);
        userLocation = userLocationRepository.save(userLocation);
        return userLocationMapper.toDto(userLocation);
    }

    @Override
    public Optional<UserLocationDTO> partialUpdate(UserLocationDTO userLocationDTO) {
        log.debug("Request to partially update UserLocation : {}", userLocationDTO);

        return userLocationRepository
            .findById(userLocationDTO.getId())
            .map(existingUserLocation -> {
                userLocationMapper.partialUpdate(existingUserLocation, userLocationDTO);

                return existingUserLocation;
            })
            .map(userLocationRepository::save)
            .map(userLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLocationDTO> findAll() {
        log.debug("Request to get all UserLocations");
        return userLocationRepository.findAll().stream().map(userLocationMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<UserLocationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userLocationRepository.findAllWithEagerRelationships(pageable).map(userLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserLocationDTO> findOne(Long id) {
        log.debug("Request to get UserLocation : {}", id);
        return userLocationRepository.findOneWithEagerRelationships(id).map(userLocationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserLocation : {}", id);
        userLocationRepository.deleteById(id);
    }
}
