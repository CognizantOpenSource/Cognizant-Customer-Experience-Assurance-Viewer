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

package com.cognizant.authapi.users.services;

import com.cognizant.authapi.base.beans.JwtSecurityConstants;
import com.cognizant.authapi.base.error.InvalidDetailsException;
import com.cognizant.authapi.base.error.UserNotFoundException;
import com.cognizant.authapi.base.services.JwtTokenService;
import com.cognizant.authapi.privacyidea.service.PrivacyIdeaService;
import com.cognizant.authapi.users.beans.TokenRequest;
import com.cognizant.authapi.users.beans.User;
import com.cognizant.authapi.users.dto.UserDTO;
import com.cognizant.authapi.users.repos.UserRepository;
import com.cognizant.authapi.users.util.UserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.cognizant.authapi.base.beans.JwtSecurityConstants.*;

/**
 * Created by 784420 on 7/18/2019 11:50 AM
 */
@Service
@Slf4j
@AllArgsConstructor
public class TokenIdService {

    private GoogleTokenValidatorService googleTokenValidatorService;
    private JwtTokenService jwtTokenService;
    private NativeUserService nativeUserService;

    private UserRepository userRepository;
    private UserUtil userUtil;

    private PrivacyIdeaService privacyIdeaService;


    public Map<String, Object> provideToken(TokenRequest tokenRequest) {
        Map<String, Object> map;
        User user = null;
        switch (tokenRequest.getType()) {
            case "oauth2":
                user = validateProviderTokenId(tokenRequest);
                break;
            case "ldap":
                break;
            case "native":
                user = validateNativeUserDetails(tokenRequest);
                break;
            default:
                break;
        }

        map = validateMFA(user, tokenRequest);

        return map;
    }

    private User validateNativeUserDetails(TokenRequest tokenRequest) {
        return nativeUserService.validateUserDetails(tokenRequest);
    }

    private User validateProviderTokenId(TokenRequest tokenRequest) {
        User googleUser = null;
        User dbUser;
        switch (tokenRequest.getProvider()) {
            case "google":
                googleUser = googleTokenValidatorService.validateGoogleToken(tokenRequest.getIdToken());
                log.info(googleUser.toString());
                break;
            case "microsoft":
                break;
            default:
                break;
        }

        if (null == googleUser) throw new InvalidDetailsException("Invalid Google token");

        Optional<User> user = userRepository.findByEmail(googleUser.getEmail());
        if (user.isPresent()) {
            dbUser = user.get();
        } else {
            throw new UserNotFoundException(googleUser.getEmail());
        }

        return dbUser;
    }

    public UserDTO getUser(TokenRequest tokenRequest) {
        User googleUser = null;
        switch (tokenRequest.getProvider()) {
            case "google":
                googleUser = googleTokenValidatorService.validateGoogleToken(tokenRequest.getIdToken());
                log.info(googleUser.toString());
                break;
            case "microsoft":
                break;
            default:
                break;
        }
        return userUtil.convertToDto(googleUser);
    }

    public Map validateMFA(User user, TokenRequest tokenRequest){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(JwtSecurityConstants.AUTH_TOKEN, "");
        map.put(JwtSecurityConstants.EXPIRES_AT, "");
        Boolean isMFA = Objects.requireNonNullElse(user.getMFA(), Boolean.FALSE);
        if (!isMFA){
            map.putAll(jwtTokenService.generateToken(user));
            jwtTokenService.validateToken((String) map.get(AUTH_TOKEN));
        } else if (isMFA && StringUtils.hasText(tokenRequest.getOtp()) && StringUtils.hasText(tokenRequest.getSessionId())){
            String message = privacyIdeaService.validateOTP(user.getEmail(), tokenRequest.getOtp(), tokenRequest.getSessionId());
            map.putAll(jwtTokenService.generateToken(user));
            jwtTokenService.validateToken((String) map.get(AUTH_TOKEN));
            map.put(MESSAGE, message);
        }else if (isMFA && (StringUtils.isEmpty(tokenRequest.getOtp()) || StringUtils.hasText(tokenRequest.getSessionId()))){
            String sessionId = UUID.randomUUID().toString();
            String message = privacyIdeaService.triggerOTPWithSessionId(user.getEmail(), sessionId);
            map.put(MESSAGE, message);
            map.put(SESSION_ID, sessionId);
        }else{
            // future implementation
        }
        return map;
    }
}
