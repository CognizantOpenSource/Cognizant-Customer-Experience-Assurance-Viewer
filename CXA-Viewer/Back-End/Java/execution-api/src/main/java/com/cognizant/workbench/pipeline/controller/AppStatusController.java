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

import com.cognizant.workbench.pipeline.dto.AppStatusDTO;
import com.cognizant.workbench.pipeline.model.AppStatus;
import com.cognizant.workbench.pipeline.service.AppStatusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 784420 on 11/20/2019 7:00 PM
 */
@RestController
@RequestMapping("/app")
@AllArgsConstructor
@Slf4j
public class AppStatusController {

    private AppStatusService service;

    /**
     * Getting all application status details
     *
     * @return list of application status details
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission(#id, 'AppStatus','appstatus.read')")
    public List<AppStatus> getAll() {
        return service.getAll();
    }

    /**
     * Getting application status details based on name
     *
     * @param name name of application
     * @return application status details
     */
    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission(#id, 'AppStatus','appstatus.read')")
    public AppStatus get(@PathVariable String name) {
        return service.get(name);
    }

    /**
     * Creating or Updating application status details based on provided data
     *
     * @param dto application status details which has to store in db
     * @return post insert/update return application status details from db
     */
    @Validated
    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission(#id, 'AppStatus','appstatus.update')")
    public AppStatus update(@Valid @RequestBody AppStatusDTO dto) {
        AppStatus appStatus = service.getOptional(dto.getName())
                .orElse(dto.getEntity());
        AppStatus updatedAppStatus = service.update(dto);
        updatedAppStatus.setRefresh(appStatus.isRefresh());
        return updatedAppStatus;
    }

    /**
     * Deleting application status details based on name
     *
     * @param name name of application
     */
    @DeleteMapping("/{name}")
    @PreAuthorize("hasPermission(#id, 'AppStatus','appstatus.delete')")
    public void delete(@RequestParam String name) {
        service.delete(name);
    }

    /**
     * Updating refresh flag to all applications
     *
     * @return refresh message
     */
    @PostMapping("/refresh")
    @PreAuthorize("hasPermission(#id, 'AppStatus','appstatus.update')")
    public Object refreshAll() {
        return service.refreshAll(true);
    }

    /**
     * updating refresh flag based on provided application name
     *
     * @param name      name of application
     * @param isRefresh refresh flag (true/false)
     * @return application status details
     */
    @PostMapping("/refresh/{name}")
    @PreAuthorize("hasPermission(#id, 'AppStatus','appstatus.update')")
    public AppStatus refreshByName(@PathVariable String name, @RequestParam boolean isRefresh) {
        return service.refreshByName(name, isRefresh);
    }

}
