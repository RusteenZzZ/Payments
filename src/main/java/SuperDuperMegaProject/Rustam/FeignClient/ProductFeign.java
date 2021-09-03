package SuperDuperMegaProject.Rustam.FeignClient;

import SuperDuperMegaProject.Rustam.Exception.FeignClientException_4xx;
import SuperDuperMegaProject.Rustam.Exception.FeignClientException_5xx;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "Product", url = "localhost:8000/", configuration = FeignConfiguration.class)
public interface ProductFeign {

    @RequestMapping(method = RequestMethod.GET, value = "/product/{id}")
    ResponseEntity<?> getProduct(@PathVariable("id") Long id);
}
