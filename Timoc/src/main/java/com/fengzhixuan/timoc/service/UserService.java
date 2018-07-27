package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.User;

public interface UserService
{
    User findUserByUsername(String username);

    User findUserByEmail(String email);

    void saveUser(User user);

    void addGold(User player, int gold);

    void removeGold(User player, int gold);

    boolean isStorageFull(User user);

    void incrementCardCount(User user);

    void decrementCardCount(User user);
}
