package SuperDuperMegaProject.Rustam.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBalanceRequest {

    String userId;
    String productId;
    String amount;
}