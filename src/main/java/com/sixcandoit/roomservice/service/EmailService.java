package com.sixcandoit.roomservice.service;

import com.sixcandoit.roomservice.entity.AccountPending;
import com.sixcandoit.roomservice.repository.AccountPendingRepository;
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

import java.time.LocalDateTime;

//gmail통해서 메일을 전달하는 서비스
@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final AccountPendingRepository accountPendingRepository;

    public String makeKey(){
        return (int)(Math.random()*9000)+1000+""; // 4자리 랜덤 숫자 생성
    }

    public void codeEmailSending(String email, int separator){
        accountPendingRepository.deleteByEmail(email);
        String authenticationCode = makeKey();
        String message = getCodeEmailHTML(authenticationCode, separator);

        AccountPending accountPending = new AccountPending();
        accountPending.setEmail(email);
        accountPending.setAuthenticationCode(authenticationCode);
        accountPendingRepository.save(accountPending); // 임시 비밀번호와 아이디 저장

        String to = email;
        String subject = "이메일 인증";
        sendEmail(to, subject, message);

    }


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

    public String getCodeEmailHTML(String authenticationCode, int separator){
        Context context = new Context();
        context.setVariable("message", authenticationCode);
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
        return templateEngine.process("admin/EmailCheckHTML", context);
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

    public void deleteOldPendingData(){

        log.info("===오래된 펜딩 메일 데이터 삭제 진행===");
        accountPendingRepository.deleteByInsDateLessThanEqual(LocalDateTime.now().minusDays(1).minusHours(1));


    }

    public Long checkEmailCode(String email, String authenticationCode){

        return accountPendingRepository.countByAuthenticationCodeAndEmail(email, authenticationCode);

    }

}


