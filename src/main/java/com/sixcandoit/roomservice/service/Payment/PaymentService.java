package com.sixcandoit.roomservice.service.Payment;

import com.sixcandoit.roomservice.dto.payment.PaymentDTO;

import com.sixcandoit.roomservice.entity.orders.PaymentEntity;
import com.sixcandoit.roomservice.repository.payment.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;



@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    public PaymentDTO read;

    public List<PaymentDTO> list(){
        List<PaymentEntity> paymentEntities=paymentRepository.findAll();

        List<PaymentDTO> paymentDTOS= Arrays.asList(modelMapper.map(paymentEntities,PaymentDTO[].class));
        return paymentDTOS;

    }


    public PaymentDTO read(Integer idx) {
        return null;
    }
}
