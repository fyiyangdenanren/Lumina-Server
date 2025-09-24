package com.kk.utils;

import com.kk.constants.HttpStatus;
import com.kk.exception.ServerException;
import jakarta.annotation.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import static com.kk.constants.RedisConstant.EMAIL_FROM;

/**
 * 发送邮件工具类
 **/
@Component
public class MailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String cc, String subject, String text) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(EMAIL_FROM);// 发送者
        smm.setTo(to);// 收件人
        smm.setCc(cc);// 抄送人
        smm.setSubject(subject);// 邮件主题
        smm.setText(text);// 邮件内容
        try {
            javaMailSender.send(smm);// 发送邮件
            System.out.println("Simple message was sent successfully.");
        } catch (MailException e) {
            System.out.println("Failed to send simple message:" + e);
            throw new ServerException(e.toString(), HttpStatus.ERROR);
        }
    }

}
