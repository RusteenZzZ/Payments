package SuperDuperMegaProject.Rustam.ReplyQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final RabbitTemplate template;

    public Producer(RabbitTemplate template){
        this.template = template;
    }

    public void reply(String respond){
        template.convertAndSend(Config.EXCHANGE_TOPIC, Config.ROUTING_KEY, respond);
    }
}
