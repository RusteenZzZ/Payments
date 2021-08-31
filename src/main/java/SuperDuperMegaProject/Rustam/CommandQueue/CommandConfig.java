package SuperDuperMegaProject.Rustam.CommandQueue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {

    public static final String QUEUE_NAME = "COMMAND_QUEUE";
    public static final String EXCHANGE_TOPIC = "COMMAND_EXCHANGE";
    public static final String ROUTING_KEY = "123BLAerBLABLA123";

    @Bean
    public Queue queue() {
        return new Queue(CommandConfig.QUEUE_NAME);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(CommandConfig.EXCHANGE_TOPIC);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(CommandConfig.ROUTING_KEY);
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