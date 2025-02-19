package com.sixcandoit.roomservice.service.office;


import com.sixcandoit.roomservice.dto.office.CalculateDTO;
import com.sixcandoit.roomservice.entity.office.CalculateEntity;
import com.sixcandoit.roomservice.repository.office.CalculateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log
@Transactional
public class CalculateService {

    private final CalculateRepository calculateRepository;
    private final ModelMapper modelMapper;

    public CalculateDTO read(Integer idx){

        Optional<CalculateEntity> calculateEntity=calculateRepository.findById(idx);

        CalculateDTO calculateDTO=modelMapper.map(calculateEntity.get(), CalculateDTO.class);

        return calculateDTO;
    }

}
