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

package com.cognizant.workbench.tools.controllers;

import com.cognizant.workbench.error.StageExistsException;
import com.cognizant.workbench.tools.beans.ToolProjectConfig;
import com.cognizant.workbench.tools.dto.ToolProjectConfigDTO;
import com.cognizant.workbench.tools.services.ProjectConfigService;
import com.cognizant.workbench.tools.util.ToolsUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 784420 on 11/7/2019 2:20 PM
 */
@RestController
@RequestMapping("/editor/config")
@AllArgsConstructor
public class ProjectConfigController {

    private ProjectConfigService services;
    private ToolsUtil util;

    /**
     * Getting all ProjectConfig details from DB
     *
     * @return List of ProjectConfig
     */
    @GetMapping("/project")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('ProjectConfig','tool.read')")
    public List<ToolProjectConfig> getAll() {
        return services.getAll();
    }

    /**
     * Getting ProjectConfig based on the provided {@code id}
     *
     * @param id id of ProjectConfig
     * @return ProjectConfig details
     */
    @GetMapping("/project/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('ProjectConfig','tool.read')")
    public ToolProjectConfig get(String id) {
        return services.get(id);
    }

    /**
     * Creating ProjectConfig based on provided details
     *
     * @param projectConfigDTO Project config detail which has to create in DB
     * @return ProjectConfig details post creation
     */
    @Valid
    @PostMapping("/project")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('ProjectConfig','tool.create')")
    public ToolProjectConfig create(@Valid @RequestBody ToolProjectConfigDTO projectConfigDTO) {
        try {
            ToolProjectConfig projectConfig = util.convertConfigDtoToEntity(projectConfigDTO, new ToolProjectConfig());
            return services.create(projectConfig);
        } catch (org.springframework.dao.DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new StageExistsException("ProjectConfig", "Id", projectConfigDTO.getName());
        }
    }

    /**
     * Updating ProjectConfig based on provided details
     *
     * @param projectConfigDTO Project config detail which has to update in DB
     * @return ProjectConfig details post Update
     */
    @Valid
    @PutMapping("/project")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasPermission('ProjectConfig','tool.update')")
    public ToolProjectConfig update(@Valid @RequestBody ToolProjectConfigDTO projectConfigDTO) {
        ToolProjectConfig projectConfig = util.convertConfigDtoToNewEntity(projectConfigDTO, new ToolProjectConfig());
        return services.update(projectConfig);
    }

    /**
     * Deleting ProjectConfig based on provided id
     *
     * @param id id of ProjectConfig
     */
    @DeleteMapping("/project")
    @ResponseStatus(HttpStatus.CONFLICT)
    @PreAuthorize("hasPermission('ProjectConfig','tool.delete')")
    public void deleteById(String id) {
        services.deleteById(id);
    }
}
