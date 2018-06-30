package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserByUsername(String username) { return userRepository.findByUsername(username); }

    @Override
    public User findUserByEmail(String email) { return userRepository.findByEmail(email); }

    @Override
    public void saveUser(User user)
    {
        userRepository.save(user);
    }
}
