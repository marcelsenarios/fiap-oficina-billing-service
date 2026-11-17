package com.oficina.billing.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.billing:billing.events.exchange}")
    private String billingExchangeName;

    @Value("${rabbitmq.exchange.os:os.events.exchange}")
    private String osExchangeName;

    @Value("${rabbitmq.queue.billing-os-events:billing.os.events.queue}")
    private String billingOsQueueName;

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public TopicExchange billingExchange() {
        return new TopicExchange(billingExchangeName, true, false);
    }

    @Bean
    public TopicExchange osExchange() {
        return new TopicExchange(osExchangeName, true, false);
    }

    @Bean
    public Queue billingOsQueue() {
        return QueueBuilder.durable(billingOsQueueName).build();
    }

    @Bean
    public Binding bindingBillingToOs(Queue billingOsQueue, TopicExchange osExchange) {
        return BindingBuilder.bind(billingOsQueue).to(osExchange).with("os.#");
    }
}
