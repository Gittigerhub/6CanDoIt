package com.sixcandoit.roomservice.service.payment;

import com.sixcandoit.roomservice.dto.orders.PaymentDTO;
import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.repository.orders.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    public List<PaymentDTO> list() {
        List<PaymentEntity>paymentEntities= paymentRepository.findAll();
        List<PaymentDTO> paymentDTOS= Arrays.asList(modelMapper.map(paymentEntities,PaymentDTO[].class));

        return paymentDTOS;

    }

    public PaymentDTO read(Integer idx) {
        Optional<PaymentEntity> paymentEntity= paymentRepository.findById(idx);
        PaymentDTO paymentDTO= modelMapper.map(paymentEntity,PaymentDTO.class);

        return paymentDTO;
    }
}
