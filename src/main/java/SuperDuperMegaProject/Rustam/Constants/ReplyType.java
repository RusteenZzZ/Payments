package SuperDuperMegaProject.Rustam.Constants;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ReplyType {
    public final String ERROR = "ERROR"; // Invalid data
    public final String FAIL = "FAIL"; // Not sufficient amount of products or balance
    public final String SUCCESS = "SUCCESS"; // Transaction completed
}