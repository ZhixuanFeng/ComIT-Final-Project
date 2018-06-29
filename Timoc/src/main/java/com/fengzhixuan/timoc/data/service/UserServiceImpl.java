package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Role;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.repository.RoleRepository;
import com.fengzhixuan.timoc.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service("userService")
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User findUserByUsername(String username) { return userRepository.findByUsername(username); }

    @Override
    public User findUserByEmail(String email) { return userRepository.findByEmail(email); }

    @Override
    public void saveUser(User user)
    {
        user.setPassword(bCryptPasswordEncoder.encode("timoc"));
        user.setEnabled(true);
        Role role = roleRepository.findByRole("USER");
        //Role role = roleRepository.getOne(1); // use getOne to increase performance, assuming id of USER is 1
        user.setRoles(new HashSet<Role>(Arrays.asList(role)));
        userRepository.save(user);
    }
}
