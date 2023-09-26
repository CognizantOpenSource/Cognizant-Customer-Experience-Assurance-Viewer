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

import com.cognizant.workbench.error.ProjectExistsExceptions;
import com.cognizant.workbench.error.ProjectNotFoundException;
import com.cognizant.workbench.jenkins.model.JenkinsDetails;
import com.cognizant.workbench.jenkins.model.JenkinsJobDetails;
import com.cognizant.workbench.jenkins.model.JenkinsRequestParam;
import com.cognizant.workbench.jenkins.service.JenkinsAPIService;
import com.cognizant.workbench.pipeline.dto.ProjectDTO;
import com.cognizant.workbench.pipeline.model.Project;
import com.cognizant.workbench.pipeline.service.ProjectMappingService;
import com.cognizant.workbench.pipeline.service.ProjectService;
import com.cognizant.workbench.pipeline.util.Util;
import com.cognizant.workbench.user.service.UserValidationService;
import com.cognizant.workbench.vcs.service.BitBucketService;
import com.cognizant.workbench.vcs.service.GitHubService;
import com.cognizant.workbench.vcs.service.GitLabService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 784420 on 6/17/2019 6:33 PM
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/projects")
public class ProjectController {

    private ProjectService projectService;
    private JenkinsAPIService jenkinsAPIService;
    private JenkinsRequestParam jenkinsRequestParam;
    private GitLabService gitLabService;
    private GitHubService gitHubService;
    private BitBucketService bitBucketService;
    private UserValidationService userValidationService;
    private ProjectMappingService projectMappingService;


    /**
     * Getting all Projects
     *
     * @return All Project details which are available in DB
     */
    @GetMapping(value = "")
    @PreAuthorize("hasPermission(#projectId, 'Project','project.read')")
    public List<Project> getAllProjects() {
        log.info("Getting all the Projects from Database.....!");
        if (userValidationService.isAdmin())
            return projectService.getAllProjects();
        return projectService.getAllProjectsForUser();
    }


    /**
     * Getting a Project by ID
     *
     * @param projectId id of Project which should get from DB
     * @return Project details which are available in DB
     */
    @GetMapping(value = "/{projectId}")
    @PreAuthorize("hasPermission(#projectId, 'Project','project.read')")
    public Project getProject(@PathVariable String projectId) {
        log.info("Getting Project id is : " + projectId);
        return projectService.assertOrGet(projectId);
    }

    /**
     * Adding a new Project
     *
     * @param project details of project
     * @return Project details post storing in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "")
    @PreAuthorize("hasPermission(#projectId, 'Project','project.create')")
    public Project addNewProject(@Valid @RequestBody ProjectDTO project) {
        System.out.println(" inside post project ");
        log.info("Saving the Project " + project.getName());
        if (projectService.getProjectByName(project.getName()).isEmpty()) {
            Project addNewProject = projectService.addNewProject(Util.mapDtoToDocument(project));
            /*insert project id in project mapping*/
            projectMappingService.insert(addNewProject.getId());
            return addNewProject;
        } else {
            throw new ProjectExistsExceptions(project.getName());
        }
    }

    /**
     * Updating a Project
     *
     * @param project details of project
     * @return Project details post storing in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{projectId}")
    @PreAuthorize("hasPermission(#projectId, 'Project','project.update')")
    public Project updateProject(@Valid @RequestBody ProjectDTO project) {
        log.info("Updating the Project data " + project.toString());
        assertProject(project.getId());
        if (!projectService.getProjectByName(project.getName()).isEmpty()) {
            return projectService.updateProject(Util.mapDtoToDocument(project));
        } else {
            throw new ProjectExistsExceptions(project.getName());
        }
    }


    /**
     * Remove project based on the Project Id
     *
     * @param projectId Id of project which need to delete from DB.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{projectId}")
    @PreAuthorize("hasPermission(#projectId, 'Project','project.delete')")
    public void removeProject(@PathVariable String projectId) {
        log.info("Getting Project id is : " + projectId);
        assertProject(projectId);
        projectService.removeProject(projectId);
        /*deleting id from project mapping*/
        projectMappingService.delete(projectId);
    }

    /**
     * Getting a Project by Name
     *
     * @param projectName Name of project to get the project based on projectName
     * @return Project details
     */
    @GetMapping(value = "/search")
    @PreAuthorize("hasPermission(#projectId, 'Project','project.read')")
    public Object getProjectByName(@RequestParam(name = "name") String projectName) {
        log.info("Getting Project Name is : " + projectName);
        return projectService.getProjectByName(projectName);
    }

    /**
     * Create Job in the Jenkins
     *
     * @param projectId id of project to create job in jenkins
     * @return Jenkins Create job response message
     */
    @PostMapping(value = "/{projectId}/createJob")
    @PreAuthorize("hasPermission(#projectId, 'Project','jenkins.job.create')")
    public ResponseEntity createJenkinsJob(@PathVariable String projectId) {
        log.info("createJenkinsJob(): Starts");
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.createJenkinsJob(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), projectId);
    }

    /**
     * Updating existing Jenkins Job with pipeline script
     *
     * @param projectId id of project to update in jenkins
     * @return Jenkins Update job response message
     */
    @PostMapping(value = "/{projectId}/updateJob")
    @PreAuthorize("hasPermission(#projectId, 'Project','jenkins.job.update')")
    public ResponseEntity updateJenkinsJob(@PathVariable String projectId) {
        log.info("updateJenkinsJob(): Starts");
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.updateJenkinsJob(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), projectId);
    }


    /**
     * Build Job in the Jenkins
     *
     * @param projectId id of project, Based on project name jenkins job will build
     * @return Jenkins build job response message
     */
    @PostMapping(value = "/{projectId}/buildJob")
    @PreAuthorize("hasPermission(#projectId, 'Project','jenkins.job.build')")
    public ResponseEntity buildJenkinsJob(@PathVariable String projectId) {
        log.info("buildJenkinsJob(): Starts");
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.buildJenkinsJob(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), projectId);
    }

    /**
     * Build Job in the Jenkins (Parameterized Build)
     *
     * @param projectId     id of project, Based on project name jenkins job will build
     * @param requestParams Parameter values to build the jenkins job.
     * @return Jenkins build job response message
     */
    @PostMapping(value = "/{projectId}/buildJobWithParams")
    @PreAuthorize("hasPermission(#projectId, 'Project','jenkins.job.build')")
    public ResponseEntity buildJobWithParams(@PathVariable String projectId, @RequestParam Map<String, String> requestParams) {
        log.info("buildJobWithParams(): Starts");
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.buildJobWithParams(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), projectId, requestParams);
    }


    /**
     * Getting Jenkins Job Details
     *
     * @param projectId id of project, Based on project name jenkins job detail will get
     * @return Jenkins Job Details
     */
    @GetMapping(value = "/{projectId}/job")
    @PreAuthorize("hasPermission(#projectId, 'Project','jenkins.job.read')")
    public JenkinsJobDetails getJenkinsJob(@PathVariable String projectId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsJob(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), projectId);
    }

    /**
     * SCM: Pushing Jenkinsfile to repo, as of now implemented for {Gitlab, GitHub and BitBucket}
     * We are not adding any permissions to SCM tools why because its doing all operations based on the their own credentials
     */

    /**
     * Pushing jenkins file into GitLab repository
     *
     * @param projectId id of project, Based on project it will generate the pipeline script and it will push the jenkins file to repo
     * @return Response message post pushing into repository
     */
    @Validated
    @PostMapping(value = "/{projectId}/gitlab/pushJenkinsFile")
    @PreAuthorize("hasPermission(#projectId, 'Project','scm.file.push')")
    public ResponseEntity gitLabPushJenkinsFile(@Valid @PathVariable String projectId) {
        return gitLabService.pushJenkinsFile(projectId);
    }

    /**
     * Pushing jenkins file into GitHub repository
     *
     * @param projectId id of project, Based on project it will generate the pipeline script and it will push the jenkins file to repo
     * @return Response message post pushing into repository
     */
    @Validated
    @PostMapping(value = "/{projectId}/github/pushJenkinsFile")
    @PreAuthorize("hasPermission(#projectId, 'Project','scm.file.push')")
    public ResponseEntity gitHubPushJenkinsFile(@Valid @PathVariable String projectId) {
        return gitHubService.pushJenkinsFile(projectId);
    }

    /**
     * Pushing jenkins file into BitBucket repository
     *
     * @param projectId id of project, Based on project it will generate the pipeline script and it will push the jenkins file to repo
     * @return Response message post pushing into repository
     */
    @Validated
    @PostMapping(value = "/{projectId}/bitbucket/pushJenkinsFile")
    @PreAuthorize("hasPermission(#projectId, 'Project','scm.file.push')")
    public ResponseEntity bitBucketPushJenkinsFile(@Valid @PathVariable String projectId) {
        return bitBucketService.pushJenkinsFile(projectId);
    }

    /**
     * @param projectId id of project
     */
    private void assertProject(String projectId) {
        if (projectService.getProject(projectId).isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }
    }
}
