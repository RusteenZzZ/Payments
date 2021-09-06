package SuperDuperMegaProject.Rustam.queueConfiguration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    public static final String REPLY_QUEUE_NAME = "REPLY_QUEUE";
    public static final String REPLY_EXCHANGE_TOPIC = "REPLY_EXCHANGE";
    public static final String REPLY_ROUTING_KEY = "REPLY_ROUTING_KEY";

    public static final String COMMAND_QUEUE_NAME = "COMMAND_QUEUE";
    public static final String COMMAND_EXCHANGE_TOPIC = "COMMAND_EXCHANGE";
    public static final String COMMAND_ROUTING_KEY = "COMMAND_ROUTING_KEY";

    public static final String DEAD_LETTER_QUEUE_NAME = "DEAD_LETTER_QUEUE_NAME";
    public static final String DEAD_LETTER_QUEUE_EXCHANGE = "DEAD_LETTER_QUEUE_EXCHANGE";
    public static final String DEAD_LETTER_QUEUE_ROUTING_KEY = "DEAD_LETTER_QUEUE_ROUTING_KEY";


    @Bean
    public Queue queueReply() {
        return new Queue(Config.REPLY_QUEUE_NAME);
    }
    @Bean
    public Queue queueCommand() {
        return QueueBuilder.durable(Config.COMMAND_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", Config.DEAD_LETTER_QUEUE_EXCHANGE)
                .build();
    }
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(Config.DEAD_LETTER_QUEUE_NAME).build();
    }

    @Bean
    public TopicExchange topicExchangeReply(){
        return new TopicExchange(Config.REPLY_EXCHANGE_TOPIC);
    }
    @Bean
    public TopicExchange topicExchangeCommand(){
        return new TopicExchange(Config.COMMAND_EXCHANGE_TOPIC);
    }
    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(Config.DEAD_LETTER_QUEUE_EXCHANGE);
    }

    @Bean
    public Binding bindingReply(@Qualifier("queueReply") Queue queue, @Qualifier("topicExchangeReply") TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(Config.REPLY_ROUTING_KEY);
    }
    @Bean
    public Binding bindingCommand(@Qualifier("queueCommand") Queue queue, @Qualifier("topicExchangeCommand") TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(Config.COMMAND_ROUTING_KEY);
    }
    @Bean
    public Binding bindingDeadLetterQueue(){
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange());
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(this.messageConverter());
        return template;
    }
}