package SuperDuperMegaProject.Rustam.Consumer;

import SuperDuperMegaProject.Rustam.DTO.Product;
import SuperDuperMegaProject.Rustam.DTO.User;
import SuperDuperMegaProject.Rustam.Entity.Transaction;
import SuperDuperMegaProject.Rustam.FeignClient.ProductFeign;
import SuperDuperMegaProject.Rustam.FeignClient.UserFeign;
//import SuperDuperMegaProject.Rustam.ReplyQueue.ReplyProducer;
import SuperDuperMegaProject.Rustam.Constants.ReplyType;
import SuperDuperMegaProject.Rustam.QueueConfiguration.Config;
import SuperDuperMegaProject.Rustam.QueueMessage.CommandQueueMessage;
import SuperDuperMegaProject.Rustam.Service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class CommandConsumer {

    private final TransactionService transactionService;
    private final UserFeign userFeign;
    private final ProductFeign productFeign;
    //    private final ReplyProducer replyProducer;
    private final ReplyType replyType;

    private Optional<User> user;
    private List<Product> products;

    private Double balance;
    private Long userId;
    private List<Long> productIds;
    private List<Integer> amountOfProducts;

    public CommandConsumer(
            TransactionService transactionService,
            UserFeign userFeign,
            ProductFeign productFeign,
//            ReplyProducer replyProducer,
            ReplyType replyType
    ){
        this.transactionService = transactionService;
        this.userFeign = userFeign;
        this.productFeign = productFeign;
//        this.replyProducer = replyProducer;
        this.replyType = replyType;
    }

    @RabbitListener(queues = Config.COMMAND_QUEUE_NAME)
    public void takeCommand(CommandQueueMessage message){
        this.userId = message.userId;
        this.productIds = message.productIds;
        this.amountOfProducts = message.amountOfProducts;

        this.user = this.userFeign.getUser(this.userId);

        // ERROR: User doesn't exist
        this.createTransactionRecords(this.replyType.ERROR);
        if(user.isEmpty()){
//            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        // ERROR: Not matching numbers of products and amounts
        this.createTransactionRecords(this.replyType.ERROR);
        if(this.productIds.size() != this.amountOfProducts.size()){
//            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        Optional<Product> product;
        Double price = 0D;

        for(int i = 0; i < this.productIds.size(); i++){

            product = this.productFeign.getProduct(productIds.get(i));

            // ERROR: Certain product doesn't exist
            this.createTransactionRecords(this.replyType.ERROR);
            if(product.isEmpty()){
//                this.replyProducer.reply(this.replyType.ERROR);
                return;
            }

            // FAIL: Not sufficient amount of products on the storage
            this.createTransactionRecords(this.replyType.FAIL);
            if(this.amountOfProducts.get(i) > product.get().amount){
//                this.replyProducer.reply(this.replyType.FAIL);
                return;
            }

            price += product.get().price * this.amountOfProducts.get(i);
        }

        // FAIL: Not sufficient balance of user
        this.createTransactionRecords(this.replyType.FAIL);
        if(this.user.get().balance < price){
//            this.replyProducer.reply(this.replyType.FAIL);
            return;
        }

        // SUCCESS: Everything is fine
        this.createTransactionRecords(this.replyType.SUCCESS);
//        this.replyProducer.reply(this.replyType.SUCCESS);
    }

    private void createTransactionRecords(String respond){
        Optional<Product> product;

        for(int i = 0; i < this.productIds.size(); i++){
            product = this.productFeign.getProduct(productIds.get(i));

            this.transactionService.addTransaction(
                    new Transaction(
                            this.userId,
                            new Date(),
                            this.productIds.get(i),
                            this.amountOfProducts.get(i),
                            product.get().price * this.amountOfProducts.get(i),
                            this.replyType.SUCCESS
                    )
            );
        }
    }
}
