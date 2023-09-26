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

import com.cognizant.authapi.privacyidea.PrivacyIdeaProperties;
import com.cognizant.authapi.privacyidea.models.MFAUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.cognizant.authapi.privacyidea.PrivacyIdeaConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Created by 784420 on 6/9/2021 6:23 AM
 */
@Component
@AllArgsConstructor
public class PrivacyIdeaResParser {

    private PrivacyIdeaProperties properties;

    public Object getAuthTokenBody() {
        return jsonStringToMap(AUTH_TOKEN_TEMPLATE, new HashMap<>());
    }

    public Object getUserJsonBody(String email){
        return jsonStringToMap(USER_JSON_TEMPLATE, Collections.singletonMap("email", email));
    }

    public Object getAccessTokenJsonBody(MFAUserDetails mfaUserDetails){
        return jsonStringToMap(ACCESS_TOKEN_JSON_TEMPLATE, mfaUserDetails.getMap());
    }

    public Object getTriggerOrVerifyOtpJsonBody(MFAUserDetails mfaUserDetails){
        return jsonStringToMap(TRIGGER_OTP_TEMPLATE, mfaUserDetails.getMap());
    }

    public String getTokenFromRes(LinkedHashMap<String, Object> authTokenRes) {
        String authToken = (String) ((LinkedHashMap<String, LinkedHashMap<String, Object>>) authTokenRes.get("result"))
                .get("value").get("token");
        return authToken;
    }

    public String getSerialFromAccessTokenRes(LinkedHashMap<String, Object> accessTokenRes) {
        String serial = String.valueOf(((LinkedHashMap<String, Object>) accessTokenRes.get("detail")).get("serial"));
        return serial;
    }

    public String getMessageFromOtpRes(LinkedHashMap<String, Object> accessTokenRes) {
        String message = String.valueOf(((LinkedHashMap<String, Object>) accessTokenRes.get("detail")).get("message"));
        return message;
    }

    public MFAUserDetails getUserDetails(LinkedHashMap<String, Object> userRes, String email) {
        String userId = String.valueOf(((LinkedHashMap<String, Object>) userRes.get("result")).get("value"));
        MFAUserDetails userDetails = new MFAUserDetails();
        userDetails.setActive(true);
        userDetails.setMFA(true);
        return userDetails.getDefaultValues(email, userId, properties);
    }

    public Map<String, String> getRequestHeader(LinkedHashMap<String, Object> authTokenRes){
        String tokenFromRes = getTokenFromRes(authTokenRes);
        return Collections.singletonMap(AUTHORIZATION, tokenFromRes);
    }

    public Object jsonStringToMap(String template, Map<String, String> extraProp) {
        try {
            Map<String, String> map = properties.getMap();
            map.putAll(extraProp);
            StringSubstitutor sub = new StringSubstitutor(map);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(sub.replace(template), Map.class);
        }catch (IOException exception) {
            exception.printStackTrace();
        }
        return "";
    }
}
