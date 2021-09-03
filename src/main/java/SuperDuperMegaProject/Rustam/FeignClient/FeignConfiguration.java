package SuperDuperMegaProject.Rustam.FeignClient;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder(){
        return new FeignDecoder();
    }
}
