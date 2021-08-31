package SuperDuperMegaProject.Rustam.ReplyQueue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReplyConfig {

    public static final String QUEUE_NAME = "REPLY_QUEUE";
    public static final String EXCHANGE_TOPIC = "REPLY_EXCHANGE";
    public static final String ROUTING_KEY = "123BLABLABLA123";

    @Bean
    public org.springframework.amqp.core.Queue queue() {
        return new org.springframework.amqp.core.Queue(ReplyConfig.QUEUE_NAME);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(ReplyConfig.EXCHANGE_TOPIC);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(ReplyConfig.ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
