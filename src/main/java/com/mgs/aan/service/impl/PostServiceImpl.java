package com.mgs.aan.service.impl;

import com.mgs.aan.domain.Post;
import com.mgs.aan.domain.enumeration.PostType;
import com.mgs.aan.repository.PostRepository;
import com.mgs.aan.service.PostService;
import com.mgs.aan.service.dto.PostDTO;
import com.mgs.aan.service.mapper.PostMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    public PostDTO save(PostDTO postDTO) {
        log.debug("Request to save Post : {}", postDTO);
        Post post = postMapper.toEntity(postDTO);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDTO update(PostDTO postDTO) {
        log.debug("Request to update Post : {}", postDTO);
        Post post = postMapper.toEntity(postDTO);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public Optional<PostDTO> partialUpdate(PostDTO postDTO) {
        log.debug("Request to partially update Post : {}", postDTO);

        return postRepository
            .findById(postDTO.getId())
            .map(existingPost -> {
                postMapper.partialUpdate(existingPost, postDTO);

                return existingPost;
            })
            .map(postRepository::save)
            .map(postMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> findAll() {
        log.debug("Request to get all Posts");
        return postRepository.findAll().stream().map(postMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<PostDTO> findAllWithEagerRelationships(Pageable pageable) {
        return postRepository.findAllWithEagerRelationships(pageable).map(postMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostDTO> findOne(Long id) {
        log.debug("Request to get Post : {}", id);
        return postRepository.findOneWithEagerRelationships(id).map(postMapper::toDto);
    }

    @Override
    public List<PostDTO> findPostByUserId(Long id) {
        return postRepository.findPostByUserId(id)
            .stream()
            .map(postMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findAllExpiredPosts() {
        LocalDate expired = LocalDate.now();

        List<Post> expiredPosts = postRepository.findAllExpiredPosts(expired);

        List<PostDTO> sortedExpiredPosts = expiredPosts.stream()
            .sorted(Comparator.comparing(Post::getTargetDate, Comparator.reverseOrder()))
            .map(postMapper::toDto)
            .collect(Collectors.toList());
        return sortedExpiredPosts;
    }

    @Override
    public List<PostDTO> findAllPostByPostType(PostType postType) {
        List<Post> posts= postRepository.findAllPostByPostType(postType);
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : posts){
            PostDTO postDTO = postMapper.toDto(post);
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Post : {}", id);
        postRepository.deleteById(id);
    }
}
