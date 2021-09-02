package SuperDuperMegaProject.Rustam.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Transaction {
    @Id
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_sequence"
    )
    private Long id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    @Column(
            name = "date",
            nullable = false
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Column(
            name = "product_id",
            nullable = false
    )
    private Long productId;

    @Column(
            name = "amount",
            nullable = false
    )
    private Integer amount;

    @Column(
            name = "price",
            nullable = false
    )
    private Double price;

    @Column(
            name = "respond",
            nullable = false
    )
    private String respond;

    public Transaction(
            Long userId,
            Date date,
            Long productId,
            Integer amount,
            Double price,
            String respond
    ) {
        this.userId = userId;
        this.date = date;
        this.productId = productId;
        this.amount = amount;
        this.price = price;
        this.respond = respond;
    }
}
