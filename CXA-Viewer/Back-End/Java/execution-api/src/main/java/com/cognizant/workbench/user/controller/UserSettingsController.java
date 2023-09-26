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

package com.cognizant.workbench.user.controller;

import com.cognizant.workbench.base.models.UserPrincipal;
import com.cognizant.workbench.charts.beans.DashboardConfig;
import com.cognizant.workbench.error.UserSettingsExistsFoundException;
import com.cognizant.workbench.error.UserSettingsNotFoundException;
import com.cognizant.workbench.jenkins.model.JenkinsDetails;
import com.cognizant.workbench.pipeline.model.ExternalAppConfigEntry;
import com.cognizant.workbench.user.dto.UserSettingsDTO;
import com.cognizant.workbench.user.model.UserSettings;
import com.cognizant.workbench.user.service.UserSettingsService;
import com.cognizant.workbench.vcs.model.common.SCMCredDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 784420 on 8/1/2019 12:21 PM
 */
@RestController
@RequestMapping("/users/settings")
@AllArgsConstructor
@Slf4j
@Validated
public class UserSettingsController {

    private UserSettingsService userSettingsService;

    /**
     * Getting UserSettings Based on Logged in user
     *
     * @return UserSettings details based on authenticated user details
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.read')")
    public UserSettings getUserSettings() {
        log.info("Getting Single User setting ");
        return userSettingsService.getUserSettings(getUserPrincipal().getEmail());
    }

    /**
     * Adding new UserSettings for not exists users
     *
     * @param userSettingsDTO details which should store in DB
     * @return userSettings details returns, post created in DB
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('UserSetting','user.setting.create')")
    public UserSettings addUserSettings(@RequestBody @Valid UserSettingsDTO userSettingsDTO) {
        log.info("Adding new User setting ");
        UserSettings userSettings = copyDtoToEntity(userSettingsDTO);
        if (!userSettingsService.isExists(userSettings))
            return userSettingsService.addUserSettings(userSettings);
        else
            throw new UserSettingsExistsFoundException("id", userSettings.getId());
    }

    /**
     * Updating UserSettings for existing users
     *
     * @param userSettingsDTO Modified UserSetting details which should store in DB
     * @return UserSettings details, post updating in DB.
     */
    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public UserSettings updateUserSettings(@RequestBody @Valid UserSettingsDTO userSettingsDTO) {
        log.info("Updating existing User setting from client");
        UserSettings userSettings = copyDtoToEntity(userSettingsDTO);
        if (userSettingsService.isExists(userSettings)) {
            return userSettingsService.updateUserSettings(userSettings);
        } else {
            throw new UserSettingsNotFoundException(userSettings.getId());
        }
    }

    /**
     * Updating jenkins details in UserSettings
     *
     * @param jenkinsDetails Jenkins details which has to store in the DB
     * @return UserSettings details, post updating in DB.
     */
    @PutMapping("/jenkins")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public UserSettings updateUserSettingsJenkins(@RequestBody @Valid JenkinsDetails jenkinsDetails) {
        log.info("Updating user settings with Jenkins details");
        UserSettings userSettings = getUserSettings();
        userSettings.setJenkins(jenkinsDetails);
        return updateUserSettings(userSettings);
    }

    /**
     * Updating jenkins details in UserSettings
     *
     * @param scmCredDetails GitLab details which has to store in the DB
     * @return UserSettings details, post updating in DB.
     */
    @PutMapping("/gitlab")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public UserSettings updateUserSettingsGitLab(@RequestBody @Valid SCMCredDetails scmCredDetails) {
        log.info("Updating user settings with GitLab details");
        UserSettings userSettings = getUserSettings();
        userSettings.setGitlab(scmCredDetails);
        return updateUserSettings(userSettings);
    }

    /**
     * Updating jenkins details in UserSettings
     *
     * @param scmCredDetails GitHub details which has to store in the DB
     * @return UserSettings details, post updating in DB.
     */
    @PutMapping("/github")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public UserSettings updateUserSettingsGitHub(@RequestBody @Valid SCMCredDetails scmCredDetails) {
        log.info("Updating user settings with GitHub details");
        UserSettings userSettings = getUserSettings();
        userSettings.setGithub(scmCredDetails);
        return updateUserSettings(userSettings);
    }

    /**
     * Updating jenkins details in UserSettings
     *
     * @param scmCredDetails BitBucket details which has to store in the DB
     * @return UserSettings details, post updating in DB.
     */
    @PutMapping("/bitbucket")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public UserSettings updateUserSettingsBitBucket(@RequestBody @Valid SCMCredDetails scmCredDetails) {
        log.info("Updating user settings with BitBucket details");
        UserSettings userSettings = getUserSettings();
        userSettings.setBitbucket(scmCredDetails);
        return updateUserSettings(userSettings);
    }

    /**
     * Updating DashboardConfig details in UserSettings
     *
     * @param dashboardConfig details of Dashboard config
     * @return UserSettings details, post updating in DB.
     */
    @PutMapping("/dashboard")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public UserSettings updateUserSettingsDashboard(@RequestBody DashboardConfig dashboardConfig) {
        log.info("Updating user settings with DashboardConfig details");
        UserSettings userSettings = getUserSettings();
        userSettings.setDashboard(dashboardConfig);
        return updateUserSettings(userSettings);
    }

    /**
     * TestConnection with reportportal
     *
     * @param reportPortal Jenkins Server details
     * @return Test connection status, its valid credentials or not
     */
    @PostMapping(value = "ext-app/report-portal/test")
    @PreAuthorize("hasPermission('UserSetting','user.setting.update')")
    public Map<String, Object> testReportPortalConnection(@RequestBody @Valid ExternalAppConfigEntry reportPortal) {
        return userSettingsService.getReportPortalUIToken(reportPortal);
    }

    /**
     * get report portal token
     *
     * @return report portal token
     */
    @GetMapping(value = "ext-app/report-portal/token")
    @PreAuthorize("hasPermission('UserSetting','user.setting.read')")
    public Map<String, Object> getReportPortalToken() {
        Map<String, Object> result = userSettingsService.getReportPortalUIToken(getUserSettings().getExternalApps().getLogAnalysis());
        Map<String, Object> response = new HashMap<>();
        response.put("url", getUserSettings().getExternalApps().getLogAnalysis().getCallbackUrl());
        response.put("token", result.get("access_token"));
        return response;
    }

    private UserPrincipal getUserPrincipal() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private UserSettings updateUserSettings(UserSettings userSettings) {
        log.info("Updating existing User setting ");
        return userSettingsService.updateUserSettings(userSettings);
    }

    private UserSettings copyDtoToEntity(UserSettingsDTO userSettingsDTO) {
        UserSettings userSettings = new UserSettings();
        BeanUtils.copyProperties(userSettingsDTO, userSettings);
        userSettings.setId(getUserPrincipal().getEmail());
        return userSettings;
    }
}
