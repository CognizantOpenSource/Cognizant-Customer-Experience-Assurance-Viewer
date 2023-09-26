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

package com.cognizant.authapi.privacyidea;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 784420 on 6/9/2021 3:34 AM
 */
@Configuration
@PropertySource("classpath:privacy-idea.properties")
@ConfigurationProperties(prefix = "app.privacy.idea")
@Data
public class PrivacyIdeaProperties {
    private String host;
    private String username;
    private String token;
    private String realm;
    private String resolver;
    private String internalToken;
    private boolean disableSSL;

    @JsonIgnore
    public Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        map.put("host", this.host);
        map.put("username", this.username);
        map.put("token", this.token);
        map.put("realm", this.realm);
        map.put("resolver", this.resolver);
        map.put("internalToken", this.internalToken);
        return map;
    }
}
