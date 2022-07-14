package com.server.authorization.application.service.abstraction;

import com.server.authorization.application.dto.MessageDto;

import javax.mail.MessagingException;

public interface MessageAdapter {

    void sendMessage(MessageDto messageDto) throws MessagingException;

}
