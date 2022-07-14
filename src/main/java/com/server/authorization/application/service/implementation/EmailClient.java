package com.server.authorization.application.service.implementation;

import com.server.authorization.application.pojo.MessageMediaTypes;
import com.server.authorization.application.dto.MessageDto;
import com.server.authorization.application.service.abstraction.MessageAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service("emailClient")
public class EmailClient implements MessageAdapter {
    @Value("${spring.mail.username}")
    private String emailDaemon;
    @Autowired
    private JavaMailSenderImpl emailClient;

    public EmailClient(JavaMailSenderImpl emailClient) {
        this.emailClient = emailClient;
    }

    public void sendMessage(MessageDto messageDto) throws MessagingException {
        MimeMessage message = emailClient.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(messageDto.getRecipient());
        helper.setFrom(emailDaemon);
        helper.setSubject(messageDto.getSubject());
        helper.setText(messageDto.getBody(), messageDto.getMediaType() == MessageMediaTypes.HTML.ordinal());
        emailClient.send(message);
    }
}
