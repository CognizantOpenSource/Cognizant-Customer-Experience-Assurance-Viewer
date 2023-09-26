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

package com.cognizant.authapi.external.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 784420 on 6/1/2021 1:36 AM
 */
@Data
public class ExternalToolConfig {
    @NotNull(message = "Type should not be null value")
    private XToolType type;
    @NotBlank(message = "Host URL should not be empty/null")
    private String hostURL;
    @NotBlank(message = "Username should not be empty/null")
    private String username;
    @NotBlank(message = "Token should not be empty/null")
    private String token;
    private Map<String, String> properties = new HashMap<>();

    @JsonIgnore
    public String getEncodedToken() {
        return new String(Base64.encodeBase64(token.getBytes()));
    }

    @JsonIgnore
    public String getDecodedToken() {
        return new String(Base64.decodeBase64(token.getBytes()));
    }

    public enum XToolType {
        ALM, JIRA
    }
}
