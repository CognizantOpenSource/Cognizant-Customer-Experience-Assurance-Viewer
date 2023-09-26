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

package com.cognizant.workbench.jenkins.model;

import com.cognizant.workbench.base.configurations.CustomBasicAuthentication;
import com.cognizant.workbench.base.models.UserPrincipal;
import com.cognizant.workbench.error.UserSettingsNotFoundException;
import com.cognizant.workbench.jenkins.service.JenkinsAPIService;
import com.cognizant.workbench.user.model.UserSettings;
import com.cognizant.workbench.user.service.UserSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 784420 on 8/6/2019 4:33 PM
 */
@Component
@AllArgsConstructor
public class JenkinsRequestParam {

    private UserSettingsService userSettingsService;
    private JenkinsAPIService jenkinsAPIService;
    private CustomBasicAuthentication customBasicAuthentication;

    public JenkinsDetails getUserJenkinsDetails() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserSettings userSettings = userSettingsService.getUserSettings(userPrincipal.getEmail());
        return userSettings.getJenkins();
    }

    public URI getBaseURI(JenkinsDetails jenkinsDetails) {
        if (StringUtils.isEmpty(jenkinsDetails.getJenkinsUrl()))
            throw new UserSettingsNotFoundException("Jenkins URL should not be empty");
        return URI.create(Objects.requireNonNull(jenkinsDetails.getJenkinsUrl()));
    }

    public Map<String, Object> getRequestHeader(JenkinsDetails jenkinsDetails) {
        if (StringUtils.isEmpty(jenkinsDetails.getUsername()) || StringUtils.isEmpty(jenkinsDetails.getToken()))
            throw new UserSettingsNotFoundException("Jenkins UserName or Password Should not be Empty");
        return addRequestHeaders(jenkinsDetails);
    }

    public Map<String, Object> getUserJenkinsRequestHeader(){
        return getRequestHeader(getUserJenkinsDetails());
    }

    private Map<String, Object> addRequestHeaders(JenkinsDetails jenkinsDetails) {
        return new HashMap<>(customBasicAuthentication.getBasicAuthentication(jenkinsDetails.getUsername(), jenkinsDetails.getToken()));
    }
}
