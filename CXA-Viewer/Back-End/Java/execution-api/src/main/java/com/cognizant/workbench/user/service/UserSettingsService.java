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

import com.cognizant.workbench.pipeline.model.ExternalAppConfigEntry;
import com.cognizant.workbench.user.model.UserSettings;
import com.cognizant.workbench.user.repo.UserSettingsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 784420 on 8/1/2019 12:31 PM
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserSettingsService {
    private UserSettingsRepository userSettingsRepository;
    private ReportPortalClient reportPortalClient;

    public List<UserSettings> getAllUserSettings() {
        return userSettingsRepository.findAll();
    }

    public UserSettings getUserSettings(String id) {
        return userSettingsRepository.findById(id)
                .orElseGet(() -> addUserSettings(generateUserSettings(id)));
    }

    public UserSettings addUserSettings(UserSettings userSettings) {
        return userSettingsRepository.insert(userSettings);
    }

    public UserSettings updateUserSettings(UserSettings userSettings) {
        return userSettingsRepository.save(userSettings);
    }

    public boolean isExists(UserSettings userSettings) {
        Optional<UserSettings> optional = userSettingsRepository.findById(userSettings.getId());
        return optional.isPresent();
    }

    public UserSettings generateUserSettings(String userId) {
        UserSettings userSettings = new UserSettings();
        userSettings.setId(userId);
        return userSettings;
    }

    public Map<String, Object> getReportPortalUIToken(ExternalAppConfigEntry reportPortal) {
        log.info("requesting report portal token from '{}'", reportPortal.getUrl());
        return reportPortalClient.getUIToken(URI.create(reportPortal.getUrl()),
                Collections.singletonMap(HttpHeaders.AUTHORIZATION, Credentials.basic("ui", "uiman")),
                reportPortal.getUsername(), reportPortal.getToken());
    }

    public void removeIds(List<String> accountIds) {
        userSettingsRepository.deleteByIdIn(accountIds);
    }

    public void removeById(String id) {
        userSettingsRepository.deleteById(id);
    }
}
