package com.sixcandoit.roomservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

//Gmail에서 이메일 전송하도록 설정
//이메일 비밀키 발급
//임시 비밀번호를 이메일로 발송
@Component
@RequiredArgsConstructor
public class GMailUtil {
    private final JavaMailSender javaMailSender;

    //받은사람주소, 제목, 내용
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        String from = "운영자<himyeongsun@gmail.com>";

        message.setFrom(from); //보내는 사람
        message.setTo(to); //받은사람
        message.setSubject(subject); //제목
        message.setText(text); //내용

        try {
            javaMailSender.send(message);  //java에서 메일 전송
        } catch(MailException e) {
            //메일 보내기 실패시
        }
    }
}
