package SuperDuperMegaProject.Rustam.Service;

import SuperDuperMegaProject.Rustam.Entity.Transaction;
import SuperDuperMegaProject.Rustam.Repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    public void addTransaction(Transaction transaction){
        this.transactionRepository.save(transaction);
    }
}
