//package com.apply.mail;
//
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ApplicationContext;
//
//import java.util.List;
//
//@SpringBootApplication
//public class MailTest {
//    public static void main(String[] args) {
//        // 啟動 Spring Boot 應用程式
//        ApplicationContext context = SpringApplication.run(MailTest.class, args);
//
//        // 取得 MailService Bean
//        MailService mailService = context.getBean(MailService.class);
//        mailService.sendPlainText(List.of("max.shao@icloud.com"), "寵愛牠平台審核結果通知",
//                "你過了啦哈哈\n 您的登入帳號為您的電子信箱\n 密碼預設為您的手機號碼");
//        System.out.println("send mail success!");
//    }
//}
