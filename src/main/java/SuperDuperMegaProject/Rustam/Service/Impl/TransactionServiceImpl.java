package SuperDuperMegaProject.Rustam.Service.Impl;

import SuperDuperMegaProject.Rustam.Entity.Transaction;
import SuperDuperMegaProject.Rustam.Repository.TransactionRepository;
import SuperDuperMegaProject.Rustam.Service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public void addTransaction(Transaction transaction){
        this.transactionRepository.save(transaction);
    }
}
