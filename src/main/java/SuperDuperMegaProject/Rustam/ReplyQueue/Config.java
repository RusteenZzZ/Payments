package SuperDuperMegaProject.Rustam.ReplyQueue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    public static final String QUEUE_NAME = "REPLY_QUEUE";
    public static final String EXCHANGE_TOPIC = "REPLY_EXCHANGE";
    public static final String ROUTING_KEY = "123BLABLABLA123";

    public org.springframework.amqp.core.Queue queue() {
        return new org.springframework.amqp.core.Queue(this.QUEUE_NAME);
    }

    public TopicExchange topicExchange(){
        return new TopicExchange(this.EXCHANGE_TOPIC);
    }

    public Binding binding(Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(this.ROUTING_KEY);
    }

    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
