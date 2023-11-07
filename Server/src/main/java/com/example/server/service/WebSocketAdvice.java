package com.example.server.service;

import com.example.server.exceptions.MessageProcessingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.messaging.simp.annotation.SendToUser;

@ControllerAdvice
public class WebSocketAdvice {

    @MessageExceptionHandler(MessageProcessingException.class)
    @SendToUser("/queue/errors")
    public String handleException(MessageProcessingException exception) {
        // Возвращаем сообщение об ошибке напрямую пользователю
        return exception.getMessage();
    }
}
