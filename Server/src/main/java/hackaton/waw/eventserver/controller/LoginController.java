package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.authentication.LoginBean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by tomek on 10/26/16.
 */


@Controller
public class LoginController {

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public ModelAndView displayLogin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView model = new ModelAndView("login");
        LoginBean loginBean = new LoginBean();
        model.addObject("loginBean", loginBean);
        return model;
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public Boolean executeLogin(@RequestParam(name = "userId")  String userId, @RequestParam(name = "accessToken") String accessToken) {
        //TODO: anything
        return null;
    }
}

