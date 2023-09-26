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

package com.cognizant.workbench.vcs.service;

import com.cognizant.workbench.base.models.LeapApiResponse;
import com.cognizant.workbench.error.FieldValueNullException;
import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.vcs.model.common.GitProjectDetails;
import com.cognizant.workbench.vcs.model.common.SCMConstants;
import com.cognizant.workbench.vcs.model.common.SCMDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 8/20/2019 11:49 AM
 */
@Slf4j
@Service
@AllArgsConstructor
@Validated
public class GitLabService implements ISCMService {

    private static final String PRIVATE_TOKEN = "PRIVATE-TOKEN";
    private static final String RESPONSE_TEMPLATE = "File pushed to GitLab Successfully. file name is: %s";
    private static final String LOG_TEMPLATE = "BaseURI: %s :requestHeader: %s :projectUrl: %s :filePath: %s :branch: %s";

    private SCMCommonService scmCommonService;

    @Override
    public ResponseEntity pushJenkinsFile(String projectId) {
        log.info("Getting Project and pipeline script based on project id");
        Map<String, Object> map = scmCommonService.validateAndReturnPipeline(projectId, SCMConstants.GITLAB);
        return pushFileToSCM((SCMDetails) map.get(SCMConstants.SCM), (String) map.get(SCMConstants.PIPELINE));
    }

    @Override
    public ResponseEntity pushFileToSCM(SCMDetails gitDetails, String pipeline) {
        log.info("Pushing file into repo");
        GitLabApi gitLabApi = new GitLabApi(gitDetails.getApiOrHostUrl(), gitDetails.getToken());
        gitLabApi.enableRequestResponseLogging(Level.ALL);
        org.gitlab4j.api.models.Project project = null;
        RepositoryFile file;

        try {
            project = gitLabApi.getProjectApi().getProject(gitDetails.getProjectOrRepoName());
            RepositoryFile repositoryFile = gitLabApi.getRepositoryFileApi().getFile(project.getId(), gitDetails.getFileName(), gitDetails.getBranch());
            repositoryFile.setEncoding(null);
            repositoryFile.setContent(pipeline);
            log.info("Updating existing file in repo");
            file = gitLabApi.getRepositoryFileApi().updateFile(project.getId(), repositoryFile, gitDetails.getBranch(), gitDetails.getCommitMessage());
        } catch (GitLabApiException exception) {
            if (null == project) throw new FieldValueNullException("invalid project details");
            if (exception.getLocalizedMessage().contains("File Not Found")) {
                try {
                    RepositoryFile repositoryFile = new RepositoryFile();
                    repositoryFile.setContent(pipeline);
                    repositoryFile.setFileName(gitDetails.getFileName());
                    repositoryFile.setFilePath(gitDetails.getFileName());
                    repositoryFile.setRef(gitDetails.getBranch());
                    log.info("creating new file in the repo");
                    file = gitLabApi.getRepositoryFileApi().createFile(project.getId(), repositoryFile, gitDetails.getBranch(), gitDetails.getCommitMessage());
                } catch (GitLabApiException e) {
                    log.error("Exception while creating file in GitLab: " + e.getLocalizedMessage(), e);
                    throw new ThrowException(e);
                }
            } else {
                throw new ThrowException(exception);
            }
        }
        LeapApiResponse response = new LeapApiResponse(LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Success",
                String.format(RESPONSE_TEMPLATE, file.getFilePath())
        );
        log.info(String.format(RESPONSE_TEMPLATE, file.getFilePath()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public Map<String, List> getProjectDetails(String baseURI, String username, String token) {
        log.info("Getting project/branch details ");
        Map<String, List> listMap = new HashMap<>();
        List<GitProjectDetails> projectDetails = new ArrayList<>();
        GitLabApi gitLabApi = new GitLabApi(baseURI, token);
        try {
            List<org.gitlab4j.api.models.Project> memberProjects = gitLabApi.getProjectApi().getMemberProjects();
            List<String> projectList = memberProjects.stream().map(org.gitlab4j.api.models.Project::getPathWithNamespace).collect(Collectors.toList());
            listMap.put("projects", projectList);
            memberProjects.forEach(project -> {
                GitProjectDetails.GitProjectDetailsBuilder builder = GitProjectDetails.builder();
                builder.projectName(project.getPathWithNamespace())
                        .repoUrl(project.getHttpUrlToRepo());
                try {
                    List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(project.getId());
                    builder.branches(branches.stream().map(Branch::getName).collect(Collectors.toList()));
                    projectDetails.add(builder.build());
                } catch (GitLabApiException e) {
                    log.error("Exception while getting project branch details" + e.getLocalizedMessage(), e);
                    throw new ThrowException(e);
                }
            });
            listMap.put("branches", projectDetails);
        } catch (GitLabApiException e) {
            log.error("Exception while getting project details" + e.getLocalizedMessage(), e);
            throw new ThrowException(e);
        }
        log.info("details: " + listMap);
        return listMap;
    }

    @Override
    public ResponseEntity testConnection(String baseURI, String username, String token) {
        log.info("Testing connection of GitLab");
        GitLabApi gitLabApi = new GitLabApi(baseURI, token);
        try {
            HttpStatus status;
            String statusMessage;
            String message;

            User currentUser = gitLabApi.getUserApi().getCurrentUser();
            if (currentUser.getUsername().equals(username)) {
                status = HttpStatus.OK;
                statusMessage = SCMConstants.SUCCESS;
                message = SCMConstants.TEST_CONNECTION_SUCCESSFUL;
            } else {
                status = HttpStatus.UNAUTHORIZED;
                statusMessage = SCMConstants.FAILED;
                message = SCMConstants.TEST_CONNECTION_FAILED + SCMConstants.USERNAME_IS_MISMATCH_WITH_TOKEN;
            }
            LeapApiResponse response = new LeapApiResponse(LocalDateTime.now(),
                    status.value(),
                    statusMessage,
                    message
            );
            log.info(message);
            return new ResponseEntity<>(response, status);
        } catch (GitLabApiException e) {
            log.error("Exception while Testing connection for GitLab " + e.getLocalizedMessage(), e);
            throw new ThrowException(e);
        }
    }
}
