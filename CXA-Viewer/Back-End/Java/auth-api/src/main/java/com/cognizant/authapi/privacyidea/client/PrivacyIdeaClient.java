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

package com.cognizant.authapi.privacyidea.client;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by 784420 on 6/9/2021 6:12 AM
 */
public interface PrivacyIdeaClient {

    @PostMapping(value = "/auth", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> getAuthToken(@RequestBody Object jsonBody);

    @PostMapping(value = "/user/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> createUser(@RequestBody Object jsonBody, @RequestHeader Map<String, String> map);

    @DeleteMapping(value = "/user/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> get(@RequestHeader Map<String, String> map,
                                             @RequestParam("resolver") String resolver,
                                             @RequestParam("email") String email
    );

    @DeleteMapping(value = "/user/{resolver}/{email}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> deleteUser(@RequestHeader Map<String, String> map,
                                             @PathVariable("resolver") String resolver,
                                             @PathVariable("email") String email
    );

    @PostMapping(value = "/token/init", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> createAccessToken(@RequestBody Object jsonBody, @RequestHeader Map<String, String> map);

    @DeleteMapping(value = "/token/{serial}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> deleteAccessToken(@PathVariable("serial") String serial, @RequestHeader Map<String, String> map);

    @PostMapping(value = "/validate/check", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    LinkedHashMap<String, Object> triggerOTP(@RequestBody Object jsonBody, @RequestHeader Map<String, String> map);
}
