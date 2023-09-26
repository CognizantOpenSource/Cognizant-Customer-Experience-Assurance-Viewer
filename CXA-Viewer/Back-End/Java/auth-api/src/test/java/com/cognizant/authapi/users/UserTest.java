package com.cognizant.authapi.users;

import com.cognizant.authapi.users.beans.User;
import com.cognizant.authapi.users.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UserTest {
    @Autowired
    UserService userService;

    @Test
    void userTest(){

    }
}
