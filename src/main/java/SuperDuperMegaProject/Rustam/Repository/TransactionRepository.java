package SuperDuperMegaProject.Rustam.Repository;

import SuperDuperMegaProject.Rustam.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
