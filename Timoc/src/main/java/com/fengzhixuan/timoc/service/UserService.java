package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.User;

public interface UserService
{
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    void saveUser(User user);
}
