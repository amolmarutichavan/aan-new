package com.mgs.aan.service.impl;

import com.mgs.aan.domain.Pincode;
import com.mgs.aan.repository.PincodeRepository;
import com.mgs.aan.service.PincodeService;
import com.mgs.aan.service.dto.PincodeDTO;
import com.mgs.aan.service.mapper.PincodeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Pincode}.
 */
@Service
@Transactional
public class PincodeServiceImpl implements PincodeService {

    private final Logger log = LoggerFactory.getLogger(PincodeServiceImpl.class);

    private final PincodeRepository pincodeRepository;

    private final PincodeMapper pincodeMapper;

    public PincodeServiceImpl(PincodeRepository pincodeRepository, PincodeMapper pincodeMapper) {
        this.pincodeRepository = pincodeRepository;
        this.pincodeMapper = pincodeMapper;
    }

    @Override
    public PincodeDTO save(PincodeDTO pincodeDTO) {
        log.debug("Request to save Pincode : {}", pincodeDTO);
        Pincode pincode = pincodeMapper.toEntity(pincodeDTO);
        pincode = pincodeRepository.save(pincode);
        return pincodeMapper.toDto(pincode);
    }

    @Override
    public PincodeDTO update(PincodeDTO pincodeDTO) {
        log.debug("Request to update Pincode : {}", pincodeDTO);
        Pincode pincode = pincodeMapper.toEntity(pincodeDTO);
        pincode = pincodeRepository.save(pincode);
        return pincodeMapper.toDto(pincode);
    }

    @Override
    public Optional<PincodeDTO> partialUpdate(PincodeDTO pincodeDTO) {
        log.debug("Request to partially update Pincode : {}", pincodeDTO);

        return pincodeRepository
            .findById(pincodeDTO.getId())
            .map(existingPincode -> {
                pincodeMapper.partialUpdate(existingPincode, pincodeDTO);

                return existingPincode;
            })
            .map(pincodeRepository::save)
            .map(pincodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PincodeDTO> findAll() {
        log.debug("Request to get all Pincodes");
        return pincodeRepository.findAll().stream().map(pincodeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PincodeDTO> findOne(Long id) {
        log.debug("Request to get Pincode : {}", id);
        return pincodeRepository.findById(id).map(pincodeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Pincode : {}", id);
        pincodeRepository.deleteById(id);
    }
}
