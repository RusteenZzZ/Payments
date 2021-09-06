package SuperDuperMegaProject.Rustam.consumer;

import SuperDuperMegaProject.Rustam.DTO.Product;
import SuperDuperMegaProject.Rustam.DTO.ResponseDto;
import SuperDuperMegaProject.Rustam.DTO.UpdateBalanceRequest;
import SuperDuperMegaProject.Rustam.DTO.User;
import SuperDuperMegaProject.Rustam.entity.Transaction;
import SuperDuperMegaProject.Rustam.feignClient.ProductFeign;
import SuperDuperMegaProject.Rustam.feignClient.UserFeign;
import SuperDuperMegaProject.Rustam.producer.ReplyProducer;
import SuperDuperMegaProject.Rustam.constants.ReplyType;
import SuperDuperMegaProject.Rustam.queueConfiguration.Config;
import SuperDuperMegaProject.Rustam.queueMessage.CommandQueueMessage;
import SuperDuperMegaProject.Rustam.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private List<Product> products;

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
        this.products = new ArrayList<Product>();
    }

    @RabbitListener(queues = Config.COMMAND_QUEUE_NAME)
    public void takeCommand(CommandQueueMessage message) throws Exception {
        this.userId = message.userId;
        this.productIds = message.productIds;
        this.amountOfProducts = message.amountOfProducts;

        this.userFeign.getUser(this.userId);

        ResponseEntity<?> userResponse;
        userResponse = this.userFeign.getUser(this.userId);

        // ERROR: User doesn't exist
        if(
                userResponse.getStatusCode().is4xxClientError()
                        || userResponse.getStatusCode().is5xxServerError()
        ){
            this._createErrorTransactionRecords();
            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        ModelMapper modelMapper = new ModelMapper();

        ResponseDto responseDto = modelMapper.map(userResponse.getBody(), ResponseDto.class);
        User user = modelMapper.map(responseDto.getBody(), User.class);

        if(user == null){
            throw new Exception("user from response is pointing to null");
        }

        // ERROR: Not matching numbers of products and amounts
        if(this.productIds.size() != this.amountOfProducts.size()){
            this._createErrorTransactionRecords();
            this.replyProducer.reply(this.replyType.ERROR);
            return;
        }

        Product product;
        Double price = 0D;

        for(int i = 0; i < this.productIds.size(); i++){

            ResponseEntity<?> productResponse = this.productFeign.getProduct(productIds.get(i));

            // ERROR: Certain product doesn't exist
            if(
                    productResponse.getStatusCode().is4xxClientError()
                            || productResponse.getStatusCode().is5xxServerError()
            ){
                this._createErrorTransactionRecords();
                this.replyProducer.reply(this.replyType.ERROR);
                return;
            }

            responseDto = modelMapper.map(productResponse.getBody(), ResponseDto.class);
            product = modelMapper.map(responseDto.getBody(), Product.class);

            if(product == null){
                throw new Exception("product form response is pointing to null");
            }

            // FAIL: Not sufficient amount of products on the storage
            if(this.amountOfProducts.get(i) > product.amount){
                this._createFailTransactionRecords();
                this.replyProducer.reply(this.replyType.FAIL);
                return;
            }

            price += product.price * this.amountOfProducts.get(i);

            this.products.add(product);
        }

        // FAIL: Not sufficient balance of user
        if(user.balance < price){
            this._createFailTransactionRecords();
            this.replyProducer.reply(this.replyType.FAIL);
            return;
        }

        // SUCCESS: Everything is fine
        this._createSuccessTransactionRecords();

        for(int i = 0; i < this.productIds.size(); i++){
            UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest(
                    String.valueOf(this.userId),
                    String.valueOf(this.productIds.get(i)),
                    String.valueOf(this.amountOfProducts.get(i))
            );

            this.userFeign.updateBalance(
                    updateBalanceRequest
            );
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
        for(int i = 0; i < this.productIds.size(); i++){
            this.transactionService.addTransaction(
                    new Transaction(
                            this.userId,
                            new Date(),
                            this.productIds.get(i),
                            this.amountOfProducts.get(i),
                            this.products.get(i).price * this.amountOfProducts.get(i),
                            this.replyType.FAIL
                    )
            );
        }
    }

    private void _createSuccessTransactionRecords(){
        for(int i = 0; i < this.productIds.size(); i++){
            this.transactionService.addTransaction(
                    new Transaction(
                            this.userId,
                            new Date(),
                            this.productIds.get(i),
                            this.amountOfProducts.get(i),
                            this.products.get(i).price * this.amountOfProducts.get(i),
                            this.replyType.SUCCESS
                    )
            );
        }
    }
}
