package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long>
{
    User findByUsername(String username);
    User findByEmail(String email);
    //User findByNumber(long id);
}