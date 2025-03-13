package com.sixcandoit.roomservice.schedular;


import com.sixcandoit.roomservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;


@Component
@RequiredArgsConstructor
public class EmailSchedular {

    private  final EmailService emailService;

    @Scheduled(cron = "0 0 0 * * *")
    public void EmailDeleteSchedular(){
        emailService.deleteOldPendingData(); //스케쥴러 등록 매일 00:00
    }
}