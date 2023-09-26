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

package com.cognizant.authapi.privacyidea.service;

import com.cognizant.authapi.base.error.InvalidDetailsException;
import com.cognizant.authapi.privacyidea.PrivacyIdeaProperties;
import com.cognizant.authapi.privacyidea.client.PrivacyIdeaClient;
import com.cognizant.authapi.privacyidea.models.MFAUserDetails;
import com.cognizant.authapi.privacyidea.models.SessionDetails;
import com.cognizant.authapi.privacyidea.repo.SessionDetailsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.cognizant.authapi.privacyidea.PrivacyIdeaConstants.*;

/**
 * Created by 784420 on 6/9/2021 6:22 AM
 */
@Slf4j
@Service
@AllArgsConstructor
public class PrivacyIdeaService {

    private PrivacyIdeaClient client;
    private PrivacyIdeaResParser parser;
    private PrivacyIdeaProperties properties;
    private MFAUserDetailsService mfaUserDetailsService;
    private SessionDetailsRepository sessionDetailsRepository;

    public String triggerOTPWithSessionId(String email, String sessionId) {
        String message = triggerOTP(email);
        sessionDetailsRepository.save(new SessionDetails(email, sessionId));
        return message;
    }

    public String validateOTP(String email, String otp, String sessionId) {
        SessionDetails sessionDetails = sessionDetailsRepository.findById(email)
                .orElseThrow(() -> new InvalidDetailsException(String.format("Session not found with mentions email(%s)", email)));
        if (!sessionId.equals(sessionDetails.getSessionId()))
            throw new InvalidDetailsException("Session Id not matching, please re-login");
        String message = verifyOTP(email, otp);
        sessionDetailsRepository.deleteById(email);
        return message;
    }

    public void enableMFA(String email, boolean isMFA) {
        if (isMFA) {
            MFAUserDetails mfaUserDetails = getAccessTokenFromDB(email);
            mfaUserDetails.setMFA(true);
            mfaUserDetailsService.save(mfaUserDetails);
        } else {
            Optional<MFAUserDetails> optional = mfaUserDetailsService.get(email);
            optional.ifPresent(mfaUserDetails -> {
                mfaUserDetails.setMFA(false);
                mfaUserDetailsService.save(mfaUserDetails);
            });
        }
    }

    private MFAUserDetails getUserFromDB(String email) {
        return mfaUserDetailsService.get(email).orElse(createUser(email));
    }

    private MFAUserDetails getAccessTokenFromDB(String email) {
        return mfaUserDetailsService.get(email).orElse(createAccessToken(email));
    }

    public Map<String, String> getAuthToken() {
        Object jsonBody = parser.getAuthTokenBody();
        LinkedHashMap<String, Object> authToken = client.getAuthToken(jsonBody);
        return parser.getRequestHeader(authToken);
    }

    public MFAUserDetails createUser(String email) {
        Map<String, String> authToken = getAuthToken();
        Object userJsonBody = parser.getUserJsonBody(email);
        LinkedHashMap<String, Object> userRes = client.createUser(userJsonBody, authToken);
        MFAUserDetails userDetails = parser.getUserDetails(userRes, email);
        return mfaUserDetailsService.save(userDetails);
    }

    public MFAUserDetails createAccessToken(String email) {
        MFAUserDetails mfaUserDetails = getUserFromDB(email);
        injectDefaultValues(mfaUserDetails);
        Object tokenJsonBody = parser.getAccessTokenJsonBody(mfaUserDetails);
        Map<String, String> authToken = getAuthToken();
        LinkedHashMap<String, Object> accessTokenRes = client.createAccessToken(tokenJsonBody, authToken);
        String serial = parser.getSerialFromAccessTokenRes(accessTokenRes);
        mfaUserDetails.setSerial(serial);
        return mfaUserDetailsService.save(mfaUserDetails);
    }

    public String triggerOTP(String email) {
        Optional<MFAUserDetails> optional = mfaUserDetailsService.get(email);
        if (optional.isEmpty())
            throw new InvalidDetailsException(String.format(MFA_IS_NOT_ENABLED_FOR_EMAIL, email));
        MFAUserDetails mfaUserDetails = optional.get();
        if (!mfaUserDetails.isMFA())
            throw new InvalidDetailsException(String.format(MFA_IS_NOT_ENABLED_FOR_EMAIL, email));
        Object otpJsonBody = parser.getTriggerOrVerifyOtpJsonBody(mfaUserDetails);
        Map<String, String> authToken = getAuthToken();
        LinkedHashMap<String, Object> accessTokenRes = client.triggerOTP(otpJsonBody, authToken);
        String message = parser.getMessageFromOtpRes(accessTokenRes);
        if (!OTP_TRIGGER_MESSAGE.equals(message))
            throw new InvalidDetailsException(message);
        return message;
    }

    public String verifyOTP(String email, String otp) {
        Optional<MFAUserDetails> optional = mfaUserDetailsService.get(email);
        if (optional.isEmpty())
            throw new InvalidDetailsException(String.format(MFA_IS_NOT_ENABLED_FOR_EMAIL, email));
        MFAUserDetails mfaUserDetails = optional.get();
        mfaUserDetails.setPin(mfaUserDetails.getPin() + otp);
        Object otpJsonBody = parser.getTriggerOrVerifyOtpJsonBody(mfaUserDetails);
        Map<String, String> authToken = getAuthToken();
        LinkedHashMap<String, Object> accessTokenRes = client.triggerOTP(otpJsonBody, authToken);
        String message = parser.getMessageFromOtpRes(accessTokenRes);
        if (StringUtils.hasText(message) && message.contains(OTP_VERIFY_MESSAGE))
            return "Success";
        throw new InvalidDetailsException(message);
    }

    public void deleteUser(String email) {
        Map<String, String> authToken = getAuthToken();
        deleteAccessToken(email);
        client.deleteUser(authToken, properties.getResolver(), email);
        mfaUserDetailsService.deleteById(email);
    }

    public void deleteAccessToken(String email) {
        mfaUserDetailsService.get(email).ifPresent(mfaUserDetails -> {
            Map<String, String> authToken = getAuthToken();
            client.deleteAccessToken(mfaUserDetails.getSerial(), authToken);
        });
    }

    private void injectDefaultValues(MFAUserDetails mfaUserDetails) {
        String pin = generateRandomString();
        mfaUserDetails.setOtpLen(OTP_LEN);
        mfaUserDetails.setTimeStep(TIME_STEP);
        mfaUserDetails.setHashlib(HASHLIB);
        mfaUserDetails.setPin(pin);
    }

    private String generateRandomString() {
        int length = 10;
        String random1 = RandomStringUtils.random(length, true, false);
        String random2 = RandomStringUtils.random(length, true, false).toUpperCase(Locale.ENGLISH);
        return random1 + random2;
    }


}
