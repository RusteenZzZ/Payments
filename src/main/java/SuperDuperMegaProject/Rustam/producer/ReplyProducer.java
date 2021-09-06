package SuperDuperMegaProject.Rustam.producer;

import SuperDuperMegaProject.Rustam.queueConfiguration.Config;
import SuperDuperMegaProject.Rustam.queueMessage.ReplyQueueMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReplyProducer {

    private final RabbitTemplate template;

    public ReplyProducer(RabbitTemplate template){
        this.template = template;
    }

    public void reply(String respond){
        ReplyQueueMessage replyQueueMessage = new ReplyQueueMessage();
        replyQueueMessage.respond = respond;

        template.convertAndSend(
                Config.REPLY_EXCHANGE_TOPIC,
                Config.REPLY_ROUTING_KEY,
                replyQueueMessage);
    }
}
