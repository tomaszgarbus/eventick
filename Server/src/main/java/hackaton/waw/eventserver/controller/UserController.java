package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.model.User;
import hackaton.waw.eventserver.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tomek on 10/26/16.
 */
@RestController
@Component
@RequestMapping("users")
public class UserController {
    @Autowired UserRepository userRepository;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public User addUser(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }
}
