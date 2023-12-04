package com.mgs.aan.service.impl;

import com.mgs.aan.domain.Taluka;
import com.mgs.aan.repository.TalukaRepository;
import com.mgs.aan.service.TalukaService;
import com.mgs.aan.service.dto.TalukaDTO;
import com.mgs.aan.service.mapper.TalukaMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Taluka}.
 */
@Service
@Transactional
public class TalukaServiceImpl implements TalukaService {

    private final Logger log = LoggerFactory.getLogger(TalukaServiceImpl.class);

    private final TalukaRepository talukaRepository;

    private final TalukaMapper talukaMapper;

    public TalukaServiceImpl(TalukaRepository talukaRepository, TalukaMapper talukaMapper) {
        this.talukaRepository = talukaRepository;
        this.talukaMapper = talukaMapper;
    }

    @Override
    public TalukaDTO save(TalukaDTO talukaDTO) {
        log.debug("Request to save Taluka : {}", talukaDTO);
        Taluka taluka = talukaMapper.toEntity(talukaDTO);
        taluka = talukaRepository.save(taluka);
        return talukaMapper.toDto(taluka);
    }

    @Override
    public TalukaDTO update(TalukaDTO talukaDTO) {
        log.debug("Request to update Taluka : {}", talukaDTO);
        Taluka taluka = talukaMapper.toEntity(talukaDTO);
        taluka = talukaRepository.save(taluka);
        return talukaMapper.toDto(taluka);
    }

    @Override
    public Optional<TalukaDTO> partialUpdate(TalukaDTO talukaDTO) {
        log.debug("Request to partially update Taluka : {}", talukaDTO);

        return talukaRepository
            .findById(talukaDTO.getId())
            .map(existingTaluka -> {
                talukaMapper.partialUpdate(existingTaluka, talukaDTO);

                return existingTaluka;
            })
            .map(talukaRepository::save)
            .map(talukaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TalukaDTO> findAll() {
        log.debug("Request to get all Talukas");
        return talukaRepository.findAll().stream().map(talukaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TalukaDTO> findOne(Long id) {
        log.debug("Request to get Taluka : {}", id);
        return talukaRepository.findById(id).map(talukaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Taluka : {}", id);
        talukaRepository.deleteById(id);
    }
}
