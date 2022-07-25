package com.server.authorization.application.dto;

import com.server.authorization.application.pojo.MessageMediaTypes;

import java.security.InvalidParameterException;
import java.util.Arrays;

public class MessageDto {
    private String recipient;
    private String subject;
    private String body;
    private int mediaType;

    private MessageDto(String recipient, String subject, String body, int mediaType) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        setMediaType(mediaType);
    }

    public static MessageDto createMessage(String recipient, String subject, String body, int mediaType) {
        return new MessageDto(recipient, subject, body, mediaType);
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        boolean isMediaType = Arrays.stream(MessageMediaTypes.values()).anyMatch(v -> v.ordinal() == mediaType);
        if(!isMediaType) throw new InvalidParameterException("Argument is not a valid media type.");
        this.mediaType = mediaType;
        return;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
