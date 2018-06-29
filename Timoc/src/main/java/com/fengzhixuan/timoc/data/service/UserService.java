package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.User;

public interface UserService
{
    public User findUserByUsername(String username);
    public User findUserByEmail(String email);
    public void saveUser(User user);
}
