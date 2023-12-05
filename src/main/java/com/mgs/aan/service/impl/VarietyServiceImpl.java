package com.mgs.aan.service.impl;

import com.mgs.aan.domain.Product;
import com.mgs.aan.domain.Variety;
import com.mgs.aan.repository.VarietyRepository;
import com.mgs.aan.service.VarietyService;
import com.mgs.aan.service.dto.VarietyDTO;
import com.mgs.aan.service.mapper.VarietyMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Variety}.
 */
@Service
@Transactional
public class VarietyServiceImpl implements VarietyService {

    private final Logger log = LoggerFactory.getLogger(VarietyServiceImpl.class);

    private final VarietyRepository varietyRepository;

    private final VarietyMapper varietyMapper;

    public VarietyServiceImpl(VarietyRepository varietyRepository, VarietyMapper varietyMapper) {
        this.varietyRepository = varietyRepository;
        this.varietyMapper = varietyMapper;
    }

    @Override
    public VarietyDTO save(VarietyDTO varietyDTO) {
        log.debug("Request to save Variety : {}", varietyDTO);
        Variety variety = varietyMapper.toEntity(varietyDTO);
        variety = varietyRepository.save(variety);
        return varietyMapper.toDto(variety);
    }

    @Override
    public VarietyDTO update(VarietyDTO varietyDTO) {
        log.debug("Request to update Variety : {}", varietyDTO);
        Variety variety = varietyMapper.toEntity(varietyDTO);
        variety = varietyRepository.save(variety);
        return varietyMapper.toDto(variety);
    }

    @Override
    public Optional<VarietyDTO> partialUpdate(VarietyDTO varietyDTO) {
        log.debug("Request to partially update Variety : {}", varietyDTO);

        return varietyRepository
            .findById(varietyDTO.getId())
            .map(existingVariety -> {
                varietyMapper.partialUpdate(existingVariety, varietyDTO);

                return existingVariety;
            })
            .map(varietyRepository::save)
            .map(varietyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VarietyDTO> findAll() {
        log.debug("Request to get all Varieties");
        return varietyRepository.findAll().stream().map(varietyMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VarietyDTO> findOne(Long id) {
        log.debug("Request to get Variety : {}", id);
        return varietyRepository.findById(id).map(varietyMapper::toDto);
    }

    @Override
    public List<VarietyDTO> findByProductId(Long id) {
        return varietyRepository.findAllByProductId(id)
            .stream()
            .map(v -> varietyMapper.toDto((Variety) v))
            .collect(Collectors.toList());
      /*  return productRepository.findAllByCategoryId(id)
            .stream()
            .map(p -> productMapper.toDto((Product) p))
            .collect(Collectors.toList());*/
        //  }
    }
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Variety : {}", id);
        varietyRepository.deleteById(id);
    }
}
