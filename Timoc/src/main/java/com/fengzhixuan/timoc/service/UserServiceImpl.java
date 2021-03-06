package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public User findUserByUsername(String username) { return userRepository.findByUsername(username); }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public User findUserByEmail(String email) { return userRepository.findByEmail(email); }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void saveUser(User user)
    {
        userRepository.save(user);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addGold(User user, int gold)
    {
        user.setGold(user.getGold() + gold);
        userRepository.save(user);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void removeGold(User user, int gold)
    {
        user.setGold(user.getGold() - gold);
        userRepository.save(user);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public boolean isStorageFull(User user)
    {
        return user.getMaxCardStorage() <= user.getCardsOwned();
    }

    @Override
    public void incrementCardCount(User user)
    {
        if (user.getCardsOwned() < user.getMaxCardStorage())
        {
            user.setCardsOwned(user.getCardsOwned() + 1);
        }
        userRepository.save(user);
    }

    @Override
    public void decrementCardCount(User user)
    {
        if (user.getCardsOwned() > 0)
        {
            user.setCardsOwned(user.getCardsOwned() - 1);
        }
        userRepository.save(user);
    }
}
