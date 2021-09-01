package SuperDuperMegaProject.Rustam.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
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

    public Transaction() {
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRespond() {
        return respond;
    }

    public void setRespond(String respond) {
        this.respond = respond;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", productId=" + productId +
                ", amount=" + amount +
                ", price=" + price +
                ", respond='" + respond + '\'' +
                '}';
    }
}
