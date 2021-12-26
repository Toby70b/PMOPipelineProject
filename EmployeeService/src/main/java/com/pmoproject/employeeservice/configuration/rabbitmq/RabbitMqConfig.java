package com.pmoproject.employeeservice.configuration.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing RabbitMQ config for the employee service
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${employeeservice.queuename}")
    private String employeeServiceQueueName;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);

        log.info("Creating connection factory with: " + username + "@" + host + ":" + port);
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }


    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange employeeServiceExchange() {
        return new DirectExchange("employeeServiceExchange", true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        //I don't really care about whether the DQL persists messages during server restarts as this isn't a real-life system
        return new DirectExchange("deadLetterExchange", false, false);
    }

    @Bean
    public Queue employeeServiceQueue() {
        return QueueBuilder.durable(employeeServiceQueueName)
                .deadLetterExchange("deadLetterExchange")
                .deadLetterRoutingKey("employeeServiceDeadLetter")
                .build();
    }

    @Bean
    public Queue employeeServiceDeadLetterQueue() {
        return QueueBuilder.nonDurable("employeeServiceDeadLetterQueue").build();
    }

    @Bean
    public Binding employeeServiceExchangeBinding() {
        return BindingBuilder.bind(employeeServiceQueue())
                .to(employeeServiceExchange()).with("employeeService");
    }

    @Bean
    public Binding employeeServiceDeadLetterBinding() {
        return BindingBuilder.bind(employeeServiceDeadLetterQueue())
                .to(deadLetterExchange()).with("employeeServiceDeadLetter");
    }

    @Bean
    FatalExceptionStrategy customExceptionStrategy() {
        return new CustomFatalExceptionStrategy();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setErrorHandler(errorHandler());
        factory.setAdviceChain(retryOperationsInterceptor().build());
        return factory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(customExceptionStrategy());
    }

    @Bean
    public RetryInterceptorBuilder.StatelessRetryInterceptorBuilder retryOperationsInterceptor() {
        RetryInterceptorBuilder.StatelessRetryInterceptorBuilder builder = RetryInterceptorBuilder.stateless();
        builder.retryPolicy(new CustomRetryPolicy(3, retryableClassifier()));
        builder.backOffPolicy(backoffPolicy());
        MessageRecoverer recoverer = new RejectAndDontRequeueRecoverer();
        builder.recoverer(recoverer);
        return builder;
    }

    @Bean
    public BackOffPolicy backoffPolicy() {
        ExponentialBackOffPolicy backoffPolicy = new ExponentialBackOffPolicy();
        backoffPolicy.setInitialInterval(3000);
        backoffPolicy.setMaxInterval(10000);
        return backoffPolicy;
    }

    @Bean
    public Map<Class<? extends Throwable>, Boolean> retryableClassifier() {
        Map<Class<? extends Throwable>, Boolean> retryableClassifier = new HashMap<>();
        retryableClassifier.put(AmqpRejectAndDontRequeueException.class, false);
        retryableClassifier.put(Exception.class, true);
        return retryableClassifier;
    }


}
