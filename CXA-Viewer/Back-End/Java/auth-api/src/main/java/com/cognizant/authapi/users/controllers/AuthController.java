/*
 *
 *   Copyright (C) 2023 - Cognizant Technology Solutions
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.cognizant.authapi.users.controllers;

import com.cognizant.authapi.users.beans.TokenRequest;
import com.cognizant.authapi.users.dto.UserDTO;
import com.cognizant.authapi.users.services.AuthService;
import com.cognizant.authapi.users.services.UserService;
import com.cognizant.authapi.users.util.UserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;
import java.util.Map;

/**
 * Created by 784420 on 7/18/2019 11:47 AM
 */
@RestController
@RequestMapping(value = "/auth")
@Slf4j
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
    private UserService userService;
    private UserUtil userUtil;

    /**
     * Generating Token based on User details or based on IdToken
     * For Native type UserName and Password and for providers IdToken should pass based on this it will work
     *
     * @param tokenRequest user details
     * @return token and expiry date with time details
     */
    @PostMapping(value = "/token")
    @Validated
    /**
     * Don't add any permissions for this end point,
     * it is generating token and open end point.
     * so ignore permissions for the same.
     */

    public Map<String, Object> validateToken(@Valid @RequestBody TokenRequest tokenRequest) {
        tokenRequest.setUsername(tokenRequest.getUsername().toLowerCase(Locale.ENGLISH));
        return authService.provideToken(tokenRequest);
    }

    /**
     * Creating or Signing up the user based on the user details provided
     *
     * @param userDTO user details to sign up
     * @return post signing up return the user details
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/signup")
    /**
     * Don't add any permissions this end point,
     * it is open end point for create user.
     * so ignore permissions for the same.
     */
    public UserDTO signUpUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("User creation requested '{}'", userDTO.getEmail());
        userDTO.setEmail(userDTO.getEmail().toLowerCase(Locale.ENGLISH));
        return userUtil.convertToDto(userService.signUpUser(userDTO));
    }

    /**
     * Signing up or creating new user based on the Third party application's (Google, Microsoft etc..) token as of now Google implemented
     *
     * @param tokenRequest third party token which is generated and return from application
     * @return post signing up based on the token will return user detail which are stored in DB
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/signup/token")
    /**
     * Don't add any permissions this end point,
     * it is open end point for create user.
     * so ignore permissions for the same.
     */
    public UserDTO tokenBasedSignUpUser(@RequestBody TokenRequest tokenRequest) {
        log.info("Token Based User SignUp");
        UserDTO dto = authService.getUser(tokenRequest);
        return signUpUser(dto);
    }

}
