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

package com.cognizant.workbench.user.service;

import com.cognizant.workbench.base.services.WhiteListed;
import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.Map;

@Validated
public interface ReportPortalClient {
    @RequestLine("POST /uat/sso/oauth/token?grant_type=password&username={username}&password={token}")
    Map<String,Object> getUIToken(@WhiteListed URI baseUrl,
                       @HeaderMap Map<String, Object> requestHeader,
                       @Param("username") String username,
                       @Param("token") String token
    );
}
