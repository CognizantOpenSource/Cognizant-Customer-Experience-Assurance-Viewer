package com.cognizant.authapi.privacyidea;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

/**
 * Created by 784420 on 6/9/2021 3:36 AM
 */
@SpringBootTest
public class PrivacyIdeaCommonTest {

    @Autowired
    PrivacyIdeaProperties properties;
    @Autowired
    Environment environment;

    @Test
    void propertiesTest() {
        System.out.println("");
    }
}
