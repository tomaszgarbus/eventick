package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.model.User;
import hackaton.waw.eventserver.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    @RequestMapping(value = "/facebook/{facebook_id}", method = RequestMethod.GET)
    public User findByFacebookId(@PathVariable(value = "facebook_id") String facebookId) {
        List<User> matchingUsers = userRepository.findAll().stream().filter(u -> u.getFacebookId().equals(facebookId)).collect(Collectors.toList());
        return matchingUsers.isEmpty() ? null : matchingUsers.get(0);
    }

    @RequestMapping(value = "/facebook_id_to_id/{facebook_id}", method = RequestMethod.GET)
    public String getIdFromFacebookId(@PathVariable(value = "facebook_id") String facebookId) {
        User user = findByFacebookId(facebookId);
        return user != null ? user.getId().toString() : null;
    }

    @RequestMapping(value = "/check_in/{user_id}/{lat}/{lng}", method = RequestMethod.POST)
    public void checkIn(@PathVariable(value = "user_id") Long userId, @PathVariable(value = "lat") Double latitude, @PathVariable(value = "lng") Double longitude) {
        User user = userRepository.findOne(userId);
        if (user != null) {
            user.setLastLatitude(latitude);
            user.setLastLongitude(longitude);
        }
        userRepository.save(user);
    }

    @RequestMapping(value = "/random_access_token", method = RequestMethod.GET)
    public String getRandomAccessToken() {
        List<User> allUsers = userRepository.findAll();
        User randomUser = allUsers.get(new Random().nextInt(allUsers.size()));
        return randomUser.getAccessToken();
    }
}
