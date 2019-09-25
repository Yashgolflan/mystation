package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.UserLogin;
import org.springframework.data.repository.CrudRepository;

public interface UserLoginRepo extends CrudRepository<UserLogin, Integer> {

    public UserLogin findOneByUserNameAndPassword(String user, String pass);

}
