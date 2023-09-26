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

import com.cognizant.workbench.pipeline.model.ProjectMapping;
import com.cognizant.workbench.pipeline.service.ProjectMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "/project-mapping")
public class ProjectMappingController {
    @Autowired
    ProjectMappingService service;

    @GetMapping("/projects/{projectId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.read')")
    public ProjectMapping get(@PathVariable String projectId){
        return service.assertOrGet(projectId);
    }

    @GetMapping("/projects")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.read')")
    public List<ProjectMapping> getAll(){
        return service.getAll();
    }

    @PostMapping("/projects/{projectId}")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.create')")
    public ProjectMapping insert(@PathVariable String projectId){
        return service.insert(projectId);
    }

    @PutMapping("/projects/{projectId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.update')")
    public ProjectMapping update(@PathVariable String projectId){
        return service.update(projectId);
    }

    @DeleteMapping("/projects/{projectId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.delete')")
    public void delete(@PathVariable String projectId){
        service.delete(projectId);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.read')")
    public List<String> getUserProjectIds(@PathVariable String userId){
        return service.getUserProjectIds(userId);
    }

    @GetMapping("/users/current-user")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.read')")
    public List<String> getCurrentUserProjectIds(){
        return service.getCurrentUserProjectIds();
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.update')")
    public List<ProjectMapping> updateWithUser(@PathVariable String userId, @RequestBody List<String> projectIds){
        return service.updateWithUser(userId, projectIds);
    }

    /*Teams*/
    @PutMapping("/teams/{teamId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.update')")
    public List<ProjectMapping> updateWithTeamId(@PathVariable String teamId, @RequestBody List<String> projectIds){
        return service.updateWithTeams(teamId, projectIds);
    }

    @GetMapping("/teams/{teamId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.read')")
    public List<String> getByTeamId(@PathVariable String teamId){
        return service.getByTeamName(teamId);
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasPermission(#projectId, 'ProjectMapping','project.delete')")
    public void deleteTeamIdFromProject(@PathVariable String teamId, @PathVariable String projectId){
        service.deleteTeamIdFromProject(projectId, teamId);
    }

}
