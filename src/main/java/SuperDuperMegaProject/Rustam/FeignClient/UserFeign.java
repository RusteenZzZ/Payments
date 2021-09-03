package SuperDuperMegaProject.Rustam.FeignClient;

import SuperDuperMegaProject.Rustam.DTO.UpdateBalanceRequest;
import SuperDuperMegaProject.Rustam.DTO.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(name = "User", url = "localhost:8000/", configuration = FeignConfiguration.class)
public interface UserFeign {

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
    ResponseEntity<?> getUser(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.POST, value = "/user/update")
    ResponseEntity<?> updateBalance(@Valid @RequestBody UpdateBalanceRequest request);
}
