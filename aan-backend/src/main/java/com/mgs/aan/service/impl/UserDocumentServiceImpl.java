package com.mgs.aan.service.impl;

import com.mgs.aan.domain.UserDocument;
import com.mgs.aan.repository.UserDocumentRepository;
import com.mgs.aan.service.UserDocumentService;
import com.mgs.aan.service.dto.UserDocumentDTO;
import com.mgs.aan.service.mapper.UserDocumentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserDocument}.
 */
@Service
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

    private final Logger log = LoggerFactory.getLogger(UserDocumentServiceImpl.class);

    private final UserDocumentRepository userDocumentRepository;

    private final UserDocumentMapper userDocumentMapper;

    public UserDocumentServiceImpl(UserDocumentRepository userDocumentRepository, UserDocumentMapper userDocumentMapper) {
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentMapper = userDocumentMapper;
    }

    @Override
    public UserDocumentDTO save(UserDocumentDTO userDocumentDTO) {
        log.debug("Request to save UserDocument : {}", userDocumentDTO);
        UserDocument userDocument = userDocumentMapper.toEntity(userDocumentDTO);
        userDocument = userDocumentRepository.save(userDocument);
        return userDocumentMapper.toDto(userDocument);
    }

    @Override
    public UserDocumentDTO update(UserDocumentDTO userDocumentDTO) {
        log.debug("Request to update UserDocument : {}", userDocumentDTO);
        UserDocument userDocument = userDocumentMapper.toEntity(userDocumentDTO);
        userDocument = userDocumentRepository.save(userDocument);
        return userDocumentMapper.toDto(userDocument);
    }

    @Override
    public Optional<UserDocumentDTO> partialUpdate(UserDocumentDTO userDocumentDTO) {
        log.debug("Request to partially update UserDocument : {}", userDocumentDTO);

        return userDocumentRepository
            .findById(userDocumentDTO.getId())
            .map(existingUserDocument -> {
                userDocumentMapper.partialUpdate(existingUserDocument, userDocumentDTO);

                return existingUserDocument;
            })
            .map(userDocumentRepository::save)
            .map(userDocumentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDocumentDTO> findAll() {
        log.debug("Request to get all UserDocuments");
        return userDocumentRepository.findAll().stream().map(userDocumentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDocumentDTO> findOne(Long id) {
        log.debug("Request to get UserDocument : {}", id);
        return userDocumentRepository.findById(id).map(userDocumentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserDocument : {}", id);
        userDocumentRepository.deleteById(id);
    }
}
