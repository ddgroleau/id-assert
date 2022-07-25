package com.server.authorization.application.service.implementation;

import com.nimbusds.jose.util.IOUtils;
import com.server.authorization.application.pojo.MessageMediaTypes;
import com.server.authorization.application.dto.MessageDto;
import com.server.authorization.application.service.abstraction.MessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.stream.Collectors;

@Service("emailClient")
public class EmailClient implements MessageAdapter {
    private static final Logger log = LoggerFactory.getLogger(EmailClient.class);
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

    public static String getHtmlEmailTemplate(String templateName, Map<String, String> templateVariables) throws IOException {
        if(templateName == null || templateName.isEmpty()) throw new InvalidParameterException("Template name is required.");

        InputStream emailHtmlStream = new ClassPathResource(templateName).getInputStream();

        if(emailHtmlStream.available() <= 0) {
            log.error("EmailClient:getHtmlEmailTemplate(): File does not exist with template name: " + templateName);
            throw new InvalidParameterException("No file found for template name.");
        }

        String emailHtml = IOUtils.readInputStreamToString(emailHtmlStream);

        for (Map.Entry<String, String> templateVariable:templateVariables.entrySet())
            emailHtml = emailHtml.replaceAll(templateVariable.getKey(),templateVariable.getValue());

        return emailHtml;
    }
}
