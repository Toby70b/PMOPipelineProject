package com.pmoproject.employeeservice.configuration.rabbitmq;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;

/**
 * Represents a strategy designed to tell rabbit what constitutes a fatal exception, a message that should not be retried
 */
public class CustomFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
    @Override
    public boolean isFatal(Throwable t){
        return t.getCause() instanceof AmqpRejectAndDontRequeueException;
    }
}
