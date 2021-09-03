package SuperDuperMegaProject.Rustam.FeignClient;

import SuperDuperMegaProject.Rustam.Exception.FeignClientException_4xx;
import SuperDuperMegaProject.Rustam.Exception.FeignClientException_5xx;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class FeignDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response){

        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");

        if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
            System.err.println(response.body());
            return new FeignClientException_5xx(response.request().url());
        } else if (HttpStatus.valueOf(response.status()).is4xxClientError()) {

            return new FeignClientException_4xx(response.request().url());
        } else {
            return new Exception("Generic exception");
        }
    }
}
