package com.pmoproject.employeeservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pmoproject.employeeservice.controllers.EmployeeServiceController;
import com.pmoproject.employeeservice.models.domain.Employee;
import com.pmoproject.employeeservice.models.domain.EmployeeMessageOutboxItem;
import com.pmoproject.employeeservice.models.rabbitmq.EmployeeServiceRequest;
import com.pmoproject.employeeservice.models.rabbitmq.EmployeeServiceResponse;
import com.pmoproject.employeeservice.models.rabbitmq.RequestType;
import com.pmoproject.employeeservice.repositories.EmployeeMessageOutboxRepository;
import com.pmoproject.employeeservice.util.JacksonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class representing the queue consumer for the employee service
 */
@Component
@Data
@Slf4j
public class EmployeeQueueListener {
    private final EmployeeMessageOutboxRepository employeeMessageOutboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final EmployeeServiceController employeeServiceController;

    /**
     * Used to instantiate an object of this class
     *
     * @param employeeMessageOutboxRepository used to interact with the EmployeeMessageOutboxItem table
     * @param rabbitTemplate                  a pre-configured rabbit template
     * @param employeeServiceController       controller for the API used to interact with various services and package responses
     */
    public EmployeeQueueListener(EmployeeMessageOutboxRepository employeeMessageOutboxRepository,
                                 RabbitTemplate rabbitTemplate, EmployeeServiceController employeeServiceController) {
        this.employeeMessageOutboxRepository = employeeMessageOutboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.employeeServiceController = employeeServiceController;
        setupConfirmCallback();
    }

    /**
     * Method used to consume the employee service queue
     *
     * @param request the request to the employee service, supports JSON format
     * @param requestType used to route the request to different API functionality see {@link RequestType}
     * @param message the message object used to retrieve information on where to send the response to
     * @throws JsonProcessingException if an error occurs during serialization/deserialization of the request/outbox item
     */
    @RabbitListener(queues = "#{@employeeServiceQueue}")
    public void listen(EmployeeServiceRequest request, @Header("requestType") RequestType requestType, Message message) throws JsonProcessingException {
        log.info("Received message [{}]", request);
        if (message.getMessageProperties().getCorrelationId() == null || message.getMessageProperties().getReplyTo() == null) {
            throw new AmqpRejectAndDontRequeueException("No CorrelationId or ReplyTo header set in message");
        }
        String correlationId = message.getMessageProperties().getCorrelationId();
        Optional<EmployeeMessageOutboxItem> employeeServiceMessage = findExistingMessageInOutbox(correlationId);
        EmployeeServiceResponse response;
        if (employeeServiceMessage.isPresent()) {
            response = JacksonUtils.convertJsonToObject(employeeServiceMessage.get().getPayload(), EmployeeServiceResponse.class);
        } else {
            response = routeRequest(request.getRequestBody(), requestType);
            saveMessageToOutbox(correlationId, response);
        }
        log.info("Sending response [{}]", response);
        sendResponseMessageToReplyToQueue(response, message);
    }

    /**
     * Routes the request to the correct method depending on the specified request type
     * @param requestPayload body of the request, will be serialized to object specific to the request type
     * @param requestType used to route the request
     * @return a <code>EmployeeServiceResponse</code> object to be sent as the response to the request
     */
    private EmployeeServiceResponse routeRequest(Object requestPayload, RequestType requestType) {
        switch (requestType) {
            case CREATE_EMPLOYEE:
                return employeeServiceController.createEmployee(JacksonUtils.convertJsonToObject(requestPayload, Employee.class));
            case GET_EMPLOYEE_BY_ID:
                return employeeServiceController.getEmployeeById(JacksonUtils.convertJsonToObject(requestPayload, Integer.class));
            default:
                throw new IllegalArgumentException(
                        String.format("Error trying to route request: Unrecognized request type [%s].", requestType));
        }
    }

    //TODO move this to a service class
    /**
     * Saves a outgoing message to the EmployeeMessageOutboxItem table to prevent duplicate actions being taken for failed messages
     * @param correlationId correlationId of the request message
     * @param payload the response body
     * @throws JsonProcessingException if error occurs during serialization of the response body
     */
    private void saveMessageToOutbox(String correlationId, Object payload) throws JsonProcessingException {
        EmployeeMessageOutboxItem outgoingMessage =
                new EmployeeMessageOutboxItem(correlationId, JacksonUtils.convertObjectToJson(payload));
        employeeMessageOutboxRepository.save(outgoingMessage);
    }
    //TODO move this to a service class
    /**
     * Attempts to find an item in the EmployeeMessageOutboxItem table by its correlation id
     * @param correlationId correlation id to match against
     * @return an optional containing an <code>EmployeeMessageOutboxItem</code> object if a matching id is found, or empty if none is found
     */
    private Optional<EmployeeMessageOutboxItem> findExistingMessageInOutbox(String correlationId) {
        return employeeMessageOutboxRepository.findEmployeeMessageOutboxItemByCorrelationId(correlationId);
    }

    /**
     * Sends a response message to reply-to queue specified in the message
     * @param response response to be sent to the reply-to queue
     * @param message details of the request message
     * @throws JsonProcessingException if error occurs when serializing the request body to JSON
     */
    private void sendResponseMessageToReplyToQueue(EmployeeServiceResponse response, Message message) throws JsonProcessingException {
        String replyToRoutingKey = message.getMessageProperties().getReplyTo();
        String correlationId = message.getMessageProperties().getCorrelationId();
        CorrelationData correlationData = new CorrelationData(correlationId);
        String responseJson = JacksonUtils.convertObjectToJson(response);
        MessagePostProcessor messagePostProcessor = responseMessage -> {
            MessageProperties messageProperties
                    = responseMessage.getMessageProperties();
            messageProperties.setCorrelationId(correlationId);
            return responseMessage;
        };
        rabbitTemplate.convertAndSend("", replyToRoutingKey, responseJson, messagePostProcessor, correlationData);
    }

    /**
     * Setup for the publisher confirm callback, to remove the corresponding entry in the EmployeeMessageOutboxItem table
     */
    private void setupConfirmCallback() {
        this.rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
            if (correlation != null) {
                log.info("Received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
                log.info("Removing outbox item with correlation id [{}]", correlation.getId());
                employeeMessageOutboxRepository.deleteEmployeeMessageOutboxItemByCorrelationId(correlation.getId());
            }
        });
    }

}
