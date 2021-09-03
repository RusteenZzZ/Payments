package SuperDuperMegaProject.Rustam.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    public Long id;
    public String username;
    public String firstName;
    public String lastName;
    public Double balance;
}