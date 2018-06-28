package com.fengzhixuan.timoc.springmvcsecurity.config;

import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class LoginController
{
    @Autowired
    private UserService userService;

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public ModelAndView login()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration()
    {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult)
    {
        ModelAndView modelAndView = new ModelAndView();
        User userNameExists = userService.findUserByUsername(user.getUsername());
        User userEmailExists = userService.findUserByEmail(user.getEmail());
        if (userNameExists != null)
        {
            bindingResult
                    .rejectValue("username", "error.user",
                            "*Username " + user.getUsername() + " is already taken");
        }
        if (userEmailExists != null)
        {
            bindingResult
                    .rejectValue("email", "error.user",
                            "*There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors())
        {
            modelAndView.setViewName("registration");
        }
        else
        {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "Registration successful. You may login now.");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }

    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public ModelAndView logout()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @RequestMapping(value="/duplicated_login", method=RequestMethod.GET)
    public ModelAndView duplicatedLogin()
    {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        String viewName = user == null ? "error" : "duplicated_login";
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    @RequestMapping(value="/hello", method = RequestMethod.GET)
    public ModelAndView home()
    {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("welcome", "Welcome " + user.getUsername());
        modelAndView.setViewName("/hello");
        return modelAndView;
    }
}
