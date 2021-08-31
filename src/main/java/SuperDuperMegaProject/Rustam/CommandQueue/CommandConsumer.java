package SuperDuperMegaProject.Rustam.CommandQueue;

import SuperDuperMegaProject.Rustam.DTO.Product;
import SuperDuperMegaProject.Rustam.DTO.User;
import SuperDuperMegaProject.Rustam.FeignClient.ProductFeign;
import SuperDuperMegaProject.Rustam.FeignClient.UserFeign;
import SuperDuperMegaProject.Rustam.ReplyQueue.ReplyProducer;
import SuperDuperMegaProject.Rustam.ReplyQueue.ReplyType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CommandConsumer {

    private final UserFeign userFeign;
    private final ProductFeign productFeign;
    private final ReplyProducer replyProducer;
    private final ReplyType replyType;

    private Optional<User> user;
    private List<Product> products;

    private Double balance;
    private Long userId;
    private List<Long> productIds;
    private List<Integer> amountOfProducts;

    public CommandConsumer(
            UserFeign userFeign,
            ProductFeign productFeign,
            ReplyProducer replyProducer,
            ReplyType replyType
    ){
        this.userFeign = userFeign;
        this.productFeign = productFeign;
        this.replyProducer = replyProducer;
        this.replyType = replyType;
    }

    @RabbitListener(queues = CommandConfig.QUEUE_NAME)
    public void takeCommand(CommandQueueMessage message){
        this.userId = message.userId;
        this.productIds = message.productIds;
        this.amountOfProducts = message.amountOfProducts;

        this.user = this.userFeign.getUser(this.userId);

        // ERROR: User doesn't exist
        if(user.isEmpty()){
            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        // ERROR: Not matching numbers of products and amounts
        if(this.productIds.size() != this.amountOfProducts.size()){
            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        Optional<Product> product;
        Double price = 0D;

        for(int i = 0; i < this.productIds.size(); i++){

            product = this.productFeign.getProduct(productIds.get(i));

            // ERROR: Certain product doesn't exist
            if(product.isEmpty()){
                this.replyProducer.reply(this.replyType.ERROR);
                return;
            }

            // FAIL: Not sufficient amount of products on the storage
            if(this.amountOfProducts.get(i) > product.get().amount){
                this.replyProducer.reply(this.replyType.FAIL);
                return;
            }

            price += product.get().price * this.amountOfProducts.get(i);
        }

        // FAIL: Not sufficient balance of user
        if(this.user.get().balance < price){
            this.replyProducer.reply(this.replyType.FAIL);
            return;
        }

        // SUCCESS: Everything is fine
        this.replyProducer.reply(this.replyType.SUCCESS);
    }
}
