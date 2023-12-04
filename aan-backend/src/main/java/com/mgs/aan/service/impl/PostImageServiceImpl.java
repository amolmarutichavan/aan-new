package com.mgs.aan.service.impl;

import com.mgs.aan.domain.PostImage;
import com.mgs.aan.repository.PostImageRepository;
import com.mgs.aan.service.PostImageService;
import com.mgs.aan.service.dto.PostImageDTO;
import com.mgs.aan.service.mapper.PostImageMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PostImage}.
 */
@Service
@Transactional
public class PostImageServiceImpl implements PostImageService {

    private final Logger log = LoggerFactory.getLogger(PostImageServiceImpl.class);

    private final PostImageRepository postImageRepository;

    private final PostImageMapper postImageMapper;

    public PostImageServiceImpl(PostImageRepository postImageRepository, PostImageMapper postImageMapper) {
        this.postImageRepository = postImageRepository;
        this.postImageMapper = postImageMapper;
    }

    @Override
    public PostImageDTO save(PostImageDTO postImageDTO) {
        log.debug("Request to save PostImage : {}", postImageDTO);
        PostImage postImage = postImageMapper.toEntity(postImageDTO);
        postImage = postImageRepository.save(postImage);
        return postImageMapper.toDto(postImage);
    }

    @Override
    public PostImageDTO update(PostImageDTO postImageDTO) {
        log.debug("Request to update PostImage : {}", postImageDTO);
        PostImage postImage = postImageMapper.toEntity(postImageDTO);
        postImage = postImageRepository.save(postImage);
        return postImageMapper.toDto(postImage);
    }

    @Override
    public Optional<PostImageDTO> partialUpdate(PostImageDTO postImageDTO) {
        log.debug("Request to partially update PostImage : {}", postImageDTO);

        return postImageRepository
            .findById(postImageDTO.getId())
            .map(existingPostImage -> {
                postImageMapper.partialUpdate(existingPostImage, postImageDTO);

                return existingPostImage;
            })
            .map(postImageRepository::save)
            .map(postImageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostImageDTO> findAll() {
        log.debug("Request to get all PostImages");
        return postImageRepository.findAll().stream().map(postImageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostImageDTO> findOne(Long id) {
        log.debug("Request to get PostImage : {}", id);
        return postImageRepository.findById(id).map(postImageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PostImage : {}", id);
        postImageRepository.deleteById(id);
    }
}
