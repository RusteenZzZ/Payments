package SuperDuperMegaProject.Rustam.FeignClient;

import SuperDuperMegaProject.Rustam.DTO.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@FeignClient(name = "Product", url = "localhost:8000/")
public interface ProductFeign {

    @RequestMapping(method = RequestMethod.GET, value = "/product/{id}")
    Optional<Product> getProduct(@PathVariable("id") Long id);
}
