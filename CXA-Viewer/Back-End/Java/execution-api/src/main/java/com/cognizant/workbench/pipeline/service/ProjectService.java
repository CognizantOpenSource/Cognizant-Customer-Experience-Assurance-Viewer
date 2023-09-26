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

package com.cognizant.workbench.pipeline.service;

import com.cognizant.workbench.error.ProjectNotFoundException;
import com.cognizant.workbench.pipeline.model.Project;
import com.cognizant.workbench.pipeline.repo.ProjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 6/17/2019 4:42 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class ProjectService {

    private ProjectRepository projectRepository;
    private MongoTemplate mongoTemplate;
    private ProjectMappingService projectMappingService;

    //Getting all Project list
    public List<Project> getAllProjects() {
        log.info("Getting all the Projects from DB .....!");
        Sort sort = Sort.by(Sort.Direction.DESC, "lastModifiedDate");
        return projectRepository.findAll(sort);
    }

    //Getting all Project list
    public List<Project> getAllProjectsForUser() {
        log.info("Getting all the Projects from DB .....!");
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(getUserProjectIds()));
        query.with(Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        return mongoTemplate.find(query, Project.class);
    }

    //Getting a Project by Project ID
    public Optional<Project> getProject(@PathVariable String projectId) {
        log.info("Getting Project based on the Id. id is : " + projectId);
        return projectRepository.findById(projectId);
    }

    public Project assertOrGet(String projectId){
        Optional<Project> project = getProject(projectId);
        if (project.isPresent()) {
            return project.get();
        } else {
            throw new ProjectNotFoundException(projectId);
        }
    }

    //Adding a new Project
    public Project addNewProject(@RequestBody Project project) {
        log.info("Saving the Project data: " + project.toString());

        return projectRepository.insert(
                newProjectFactory(project)
        );
    }

    // Remove Project based on the Project Id
    public void removeProject(String projectId) {
        projectRepository.deleteById(projectId);
    }

    //Getting a Project by Project Name
    public List<Project> getProjectByName(@PathVariable String projectName) {
        log.info("Getting Project based on the Id. id is : " + projectName);
        return projectRepository.findByName(projectName);
    }

    //Updating a Project
    public Project updateProject(Project project) {
        log.info("Updating the Project data: " + project.toString());
        return projectRepository.save(
                updateProjectFactory(project)
        );
    }

    private List<Project> getProjectByIds(List<String> ids) {
        return (List<Project>) projectRepository.findAllById(ids);
    }

    public List<String> getUserProjectJobNames() {
        List<Project> projects = getProjectByIds(getUserProjectIds());
        return projects.stream().map(Project::getName).collect(Collectors.toList());
    }

    private List<String> getUserProjectIds() {
        return projectMappingService.getCurrentUserProjectIds();
    }

    private Project newProjectFactory(Project project) {
        project.setCreatedDate(null);
        project.setCreatedUser(null);
        project.setLastModifiedDate(null);
        project.setLastModifiedUser(null);
        return project;
    }

    private Project updateProjectFactory(Project project) {
        project.setLastModifiedDate(new Date());
        project.setLastModifiedUser(null);
        return project;
    }
}
