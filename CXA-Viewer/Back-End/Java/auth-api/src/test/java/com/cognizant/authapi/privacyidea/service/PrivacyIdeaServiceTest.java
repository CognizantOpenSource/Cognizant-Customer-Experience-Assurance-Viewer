package com.cognizant.authapi.privacyidea.service;

import com.cognizant.authapi.privacyidea.models.MFAUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by 784420 on 6/9/2021 6:38 AM
 */
@SpringBootTest
class PrivacyIdeaServiceTest {
    private static String email = "anji.ram923@gmail.com";

    @Autowired
    PrivacyIdeaService service;

    @Test
    void getAuthToken() {
        Map<String, String> authToken = service.getAuthToken();
        System.out.println(authToken);
    }

    @Test
    void createUser() {
        MFAUserDetails user = service.createUser(email);
        System.out.println();
    }

    @Test
    void deleteUser() {
        service.deleteUser(email);
        System.out.println();
    }

    @Test
    void createAccessToken() {
        MFAUserDetails mfaUserDetails = service.createAccessToken(email);
        System.out.println();
    }

    @Test
    void triggerOTP() {
        String s = service.triggerOTP(email);
        System.out.println(s);
    }

    @Test
    void verifyOTP() {
        String otp = "574229";
        String s = service.verifyOTP(email, otp);
        System.out.println(s);
    }
}