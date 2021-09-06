package SuperDuperMegaProject.Rustam.service.impl;

import SuperDuperMegaProject.Rustam.entity.Transaction;
import SuperDuperMegaProject.Rustam.repository.TransactionRepository;
import SuperDuperMegaProject.Rustam.service.TransactionService;
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
