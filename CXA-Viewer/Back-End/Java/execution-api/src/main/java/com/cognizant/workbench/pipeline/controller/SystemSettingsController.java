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

package com.cognizant.workbench.pipeline.controller;

import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.pipeline.model.BaseConstants;
import com.cognizant.workbench.pipeline.model.Link;
import com.cognizant.workbench.pipeline.model.SystemSetting;
import com.cognizant.workbench.pipeline.service.SystemSettingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Created by 784420 on 9/27/2019 6:24 PM
 */
@RestController
@RequestMapping("/system/settings")
@AllArgsConstructor
@Slf4j
public class SystemSettingsController {

    private SystemSettingService service;

    @GetMapping("")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.read')")
    public List<SystemSetting> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.read')")
    public SystemSetting getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/leap")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.read')")
    public SystemSetting get() {
        return service.getById(BaseConstants.LEAP);
    }

    @PostMapping("/reload-whitelist")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.create')")
    public ResponseEntity<?> reloadWhitelist() {
        service.reloadWhitelist();
        return new ResponseEntity<>(null, null, HttpStatus.CREATED);
    }

    @Validated
    @PutMapping("/scm")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.update')")
    public SystemSetting updateSCMSelection(@Valid @RequestBody List<String> scmList) {
        SystemSetting systemSetting = getById(BaseConstants.LEAP);
        if (CollectionUtils.isEmpty(scmList)) {
            throw new ThrowException(BaseConstants.LIST_SHOULD_NOT_BE_EMPTY);
        }
        Map<String, List<String>> scm = systemSetting.getScm();
        scm.put(BaseConstants.SELECTED, scmList);
        systemSetting.setScm(scm);
        return service.update(systemSetting);
    }

    @Validated
    @PutMapping("/externalLink")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.update')")
    public SystemSetting updateLinks(@Valid @RequestBody List<Link> links) {
        SystemSetting systemSetting = getById(BaseConstants.LEAP);
        if (CollectionUtils.isEmpty(links)) {
            throw new ThrowException(BaseConstants.LIST_SHOULD_NOT_BE_EMPTY);
        }
        systemSetting.setExternalLinks(links);
        return service.update(systemSetting);
    }

    @Validated
    @PutMapping("/login")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('SystemSetting','system.settings.update')")
    public SystemSetting updateLoginSelection(@Valid @RequestBody List<String> loginList) {
        SystemSetting systemSetting = getById(BaseConstants.LEAP);
        if (CollectionUtils.isEmpty(loginList)) {
            throw new ThrowException(BaseConstants.LIST_SHOULD_NOT_BE_EMPTY);
        }
        Map<String, List<String>> login = systemSetting.getLogin();
        login.put(BaseConstants.SELECTED, loginList);
        systemSetting.setLogin(login);
        return service.update(systemSetting);
    }

}
