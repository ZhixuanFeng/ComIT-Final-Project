package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.data.repository.RoleRepository;
import com.fengzhixuan.timoc.service.CardCollectionService;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;

@Controller
public class LoginController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardCollectionService cardCollectionService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
            // no problem, set up and save user
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            Role role = roleRepository.findByRole("USER");
            //Role role = roleRepository.getOne(1); // use getOne to increase performance, assuming id of USER is 1
            user.setRoles(new HashSet<>(Arrays.asList(role)));

            // return message
            modelAndView.addObject("successMessage", "Registration successful. You may login now.");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

            // create associated card collection
            CardCollection cardCollection = new CardCollection(user.getId());

            userService.saveUser(user);
            cardCollectionService.saveCardCollection(cardCollection);

            // create associated card deck
            CardDeck cardDeck = new CardDeck(user.getId());
            cardDeckService.initializeDeck(cardDeck);  // this saves card deck
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
}
