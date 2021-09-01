package SuperDuperMegaProject.Rustam.Producer;

import SuperDuperMegaProject.Rustam.QueueConfiguration.Config;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReplyProducer {

    private final RabbitTemplate template;

    public ReplyProducer(RabbitTemplate template){
        this.template = template;
    }

    public void reply(String respond){
        template.convertAndSend(Config.REPLY_EXCHANGE_TOPIC, Config.REPLY_ROUTING_KEY, respond);
    }
}
