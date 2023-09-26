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

package com.cognizant.authapi.privacyidea.models;

import com.cognizant.authapi.base.beans.BaseModel;
import com.cognizant.authapi.privacyidea.PrivacyIdeaProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 784420 on 6/10/2021 3:02 AM
 */
@Document(collection = "mfaUserDetails")
@Data
@EqualsAndHashCode(callSuper = false)
public class MFAUserDetails extends BaseModel {
    @Id
    private String email;
    private String userId;
    private String token;
    private String resolver;
    private String realm;
    private String pin;
    private String hashlib;
    private String serial;
    private int timeStep;
    private int otpLen;
    private boolean isMFA;
    private boolean active;

    @JsonIgnore
    public Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        map.put("email", this.email);
        map.put("otplen", String.valueOf(this.otpLen));
        map.put("pin", String.valueOf(this.pin));
        map.put("hashlib", String.valueOf(this.hashlib));
        map.put("timeStep", String.valueOf(this.timeStep));
        map.put("internalToken", this.token);
        map.put("realm", this.realm);
        map.put("resolver", this.resolver);
        return map;
    }

    @JsonIgnore
    public MFAUserDetails getDefaultValues(String email, String userId, PrivacyIdeaProperties properties){
        this.email = email;
        this.userId = userId;
        this.resolver = properties.getResolver();
        this.realm = properties.getRealm();
        this.token = properties.getInternalToken();
        return this;
    }
}
