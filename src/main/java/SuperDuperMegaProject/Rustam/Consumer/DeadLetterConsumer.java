package SuperDuperMegaProject.Rustam.Consumer;

import SuperDuperMegaProject.Rustam.QueueConfiguration.Config;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {

    @RabbitListener(queues = Config.DEAD_LETTER_QUEUE_NAME)
    public void logDeadLetter(Object message){
        System.out.println("Failed to receive the following message: ");
        System.out.println(message);
    }
}
