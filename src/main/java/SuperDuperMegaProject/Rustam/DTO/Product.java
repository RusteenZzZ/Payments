package SuperDuperMegaProject.Rustam.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    public Long id;
    public String productName;
    public Double price;
    public Integer amount;
}