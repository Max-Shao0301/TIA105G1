package com.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MailService {

    @Autowired
    private final JavaMailSender javaMailSender;


    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPlainText(Collection<String> receivers, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receivers.toArray(new String[0]));// 設定收件人
        message.setSubject(subject);// 設定標題
        message.setText(content);// 設定內容
        message.setFrom("寵愛牠<a0912622502@gmail.com>"); // 設定發件人 (與 SMTP 設定一致)

        javaMailSender.send(message);
    }

}
