package SuperDuperMegaProject.Rustam.Consumer;

import SuperDuperMegaProject.Rustam.DTO.Product;
import SuperDuperMegaProject.Rustam.DTO.UpdateBalanceRequest;
import SuperDuperMegaProject.Rustam.DTO.User;
import SuperDuperMegaProject.Rustam.Entity.Transaction;
import SuperDuperMegaProject.Rustam.FeignClient.ProductFeign;
import SuperDuperMegaProject.Rustam.FeignClient.UserFeign;
import SuperDuperMegaProject.Rustam.Producer.ReplyProducer;
import SuperDuperMegaProject.Rustam.Constants.ReplyType;
import SuperDuperMegaProject.Rustam.QueueConfiguration.Config;
import SuperDuperMegaProject.Rustam.QueueMessage.CommandQueueMessage;
import SuperDuperMegaProject.Rustam.Service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class CommandConsumer {

    private final TransactionService transactionService;
    private final UserFeign userFeign;
    private final ProductFeign productFeign;
    private final ReplyProducer replyProducer;
    private final ReplyType replyType;

    private Long userId;
    private List<Long> productIds;
    private List<Integer> amountOfProducts;

    public CommandConsumer(
            TransactionService transactionService,
            UserFeign userFeign,
            ProductFeign productFeign,
            ReplyProducer replyProducer,
            ReplyType replyType
    ){
        this.transactionService = transactionService;
        this.userFeign = userFeign;
        this.productFeign = productFeign;
        this.replyProducer = replyProducer;
        this.replyType = replyType;
    }

    @RabbitListener(queues = Config.COMMAND_QUEUE_NAME)
    public void takeCommand(CommandQueueMessage message) throws Exception {
        this.userId = message.userId;
        this.productIds = message.productIds;
        this.amountOfProducts = message.amountOfProducts;

        ResponseEntity<?> userResponse = this.userFeign.getUser(this.userId);

        // ERROR: User doesn't exist
        if(userResponse.getStatusCode().is4xxClientError()){
            this.replyProducer.reply(this.replyType.ERROR);
            throw new Exception("Get user: Client Error");
        }else if (userResponse.getStatusCode().is5xxServerError()){
            this.replyProducer.reply(this.replyType.ERROR);
            throw new Exception("Get user: Server error");
        }

        User user = (User) userResponse.getBody();

        if(user == null){
            throw new Exception("user from response is pointing to null");
        }

        // ERROR: Not matching numbers of products and amounts
        this._createErrorTransactionRecords();
        if(this.productIds.size() != this.amountOfProducts.size()){
            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        Optional<Product> product;
        Double price = 0D;

        for(int i = 0; i < this.productIds.size(); i++){

            product = this.productFeign.getProduct(productIds.get(i));

            // ERROR: Certain product doesn't exist
            this._createErrorTransactionRecords();
            if(product.isEmpty()){
                this.replyProducer.reply(this.replyType.ERROR);
                return;
            }

            // FAIL: Not sufficient amount of products on the storage
            this._createFailTransactionRecords();
            if(this.amountOfProducts.get(i) > product.get().amount){
                this.replyProducer.reply(this.replyType.FAIL);
                return;
            }

            price += product.get().price * this.amountOfProducts.get(i);
        }

        // FAIL: Not sufficient balance of user
        this._createFailTransactionRecords();
        if(user.balance < price){
            this.replyProducer.reply(this.replyType.FAIL);
            return;
        }

        // SUCCESS: Everything is fine
        this._createSuccessTransactionRecords();

        for(int i = 0; i < this.productIds.size(); i++){
            ResponseEntity<?> result = this.userFeign.updateBalance(
                    new UpdateBalanceRequest(
                            this.userId + "",
                            this.productIds.get(i) + "",
                            this.amountOfProducts.get(i) + ""
                    )
            );
            if(result.getStatusCode().is4xxClientError()){
                throw new Exception("Update user balance: Client error");
            }else if (result.getStatusCode().is5xxServerError()){
                throw new Exception("Update user balance: Server error");
            }
        }

        this.replyProducer.reply(this.replyType.SUCCESS);
    }

    private void _createErrorTransactionRecords(){
        for (Long productId : this.productIds) {
            this.transactionService.addTransaction(
                    new Transaction(
                            this.userId,
                            new Date(),
                            productId,
                            null,
                            null,
                            this.replyType.ERROR
                    )
            );
        }
    }

    private void _createFailTransactionRecords(){
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
                            this.replyType.FAIL
                    )
            );
        }
    }

    private void _createSuccessTransactionRecords(){
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
