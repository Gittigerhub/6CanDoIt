package com.sixcandoit.roomservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

//gmail통해서 메일을 전달하는 서비스
@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    //받은사람주소, 제목, 내용
    public void sendEmail(String to, String subject, String message) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            String from = "운영자<himyeongsun@gmail.com>";

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setText(message, true);

            javaMailSender.send(mimeMessage);

            log.info("send email 접근");


        } catch (MessagingException e) {
            System.out.println("전송오류");

        }

    }

    public String getTempEmailHTML(String password, int separator) {
        Context context = new Context(  );
        context.setVariable("message", password);
        String prefixUrl = "http://localhost:8080/";
        String suffixUrl = "/login";
        if(separator == 1){
            context.setVariable("forwardUrl", prefixUrl+"admin"+suffixUrl);
        }
        else if(separator == 2){

            context.setVariable("forwardUrl", prefixUrl+"member"+suffixUrl);
        }
        else {
            log.info("없는 경로 구분자");
        }
        log.info("이메일 접근");
        return templateEngine.process("admin/TempEmailHTML", context);
    }

}


