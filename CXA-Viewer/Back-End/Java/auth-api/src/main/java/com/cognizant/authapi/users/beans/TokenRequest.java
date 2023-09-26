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

package com.cognizant.authapi.users.beans;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by 784420 on 7/19/2019 3:21 PM
 */
@Data
public class TokenRequest {
    @NotBlank(message = "Type should not be empty")
    private String type;
    private String provider;
    private String username;
    private String password;
    private String idToken;

    /*MFA level details*/
    private String sessionId;
    private String otp;
}
