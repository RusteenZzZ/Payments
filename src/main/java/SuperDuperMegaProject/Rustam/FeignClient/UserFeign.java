package SuperDuperMegaProject.Rustam.FeignClient;

import SuperDuperMegaProject.Rustam.DTO.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "User", url = "localhost:8000/")
public interface UserFeign {

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
    User getUser(@PathVariable("id") Long id);
}
