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
import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.vcs.model.common.GitProjectDetails;
import com.cognizant.workbench.vcs.model.common.SCMConstants;
import com.cognizant.workbench.vcs.model.common.SCMDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 8/29/2019 6:02 PM
 */
@Service
@Slf4j
@AllArgsConstructor
@Validated
public class GitHubService implements ISCMService {

    private static final String RESPONSE_TEMPLATE = "File pushed to GitHub Successfully. file Html Url %s";
    private SCMCommonService scmCommonService;

    @Override
    public ResponseEntity pushJenkinsFile(String projectId) {
        log.info("Getting Project and pipeline script based on project id");
        Map<String, Object> map = scmCommonService.validateAndReturnPipeline(projectId, SCMConstants.GITHUB);
        return pushFileToSCM((SCMDetails) map.get(SCMConstants.SCM), (String) map.get(SCMConstants.PIPELINE));
    }

    @Override
    public ResponseEntity pushFileToSCM(SCMDetails gitDetails, String pipeline) {
        log.info("Pushing file into GitHub Repo");
        GHContentUpdateResponse contentUpdateResponse;
        GitHub gitHub;
        GHRepository ghRepository = null;
        try {
            gitHub = GitHub.connectUsingOAuth(gitDetails.getApiOrHostUrl(), gitDetails.getToken());
            ghRepository = gitHub.getRepository(gitDetails.getProjectOrRepoName());
            GHContent fileContent = ghRepository.getFileContent(gitDetails.getFileName(), gitDetails.getBranch());

            log.info("file already exist in repo, updating file with new content");
            contentUpdateResponse = ghRepository.createContent().message(gitDetails.getCommitMessage()).path(gitDetails.getFileName()).content(pipeline).branch(gitDetails.getBranch()).sha(fileContent.getSha()).commit();
        } catch (GHFileNotFoundException e) {
            if (null == ghRepository) throw new NullPointerException();
            try {
                log.info("creating new file in repo");
                contentUpdateResponse = ghRepository.createContent().message(gitDetails.getCommitMessage()).path(gitDetails.getFileName()).content(pipeline).branch(gitDetails.getBranch()).commit();
            } catch (IOException e1) {
                log.error("Exception while pushing file(Create) into GitHub: " + e.getLocalizedMessage());
                throw new ThrowException(e);
            }
        } catch (IOException e) {
            log.error("Exception while pushing file(Update) into GitHub: " + e.getLocalizedMessage());
            throw new ThrowException(e);
        }
        LeapApiResponse response = new LeapApiResponse(LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Success",
                String.format(RESPONSE_TEMPLATE, contentUpdateResponse.getContent().getHtmlUrl())
        );
        log.info(String.format(RESPONSE_TEMPLATE, contentUpdateResponse.getContent().getHtmlUrl()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public Map<String, List> getProjectDetails(String strBaseUri, String username, String oauthToken) {
        log.info("getting project/branch details");
        Map<String, List> listMap = new HashMap<>();
        List<GitProjectDetails> projectDetails = new ArrayList<>();
        try {
            GitHub gitHub = GitHub.connectUsingOAuth(strBaseUri, oauthToken);
            GHMyself myself = gitHub.getMyself();
            Map<String, GHRepository> allRepositories = myself.getAllRepositories();

            listMap.put("projects", allRepositories.values().stream().map(GHRepository::getFullName).collect(Collectors.toList()));
            allRepositories.keySet().forEach(s -> {
                try {
                    GHRepository repository = allRepositories.get(s);
                    GitProjectDetails build = GitProjectDetails.builder()
                            .projectName(repository.getFullName())
                            .repoUrl(repository.getGitTransportUrl())
                            .branches(new ArrayList<>(repository.getBranches().keySet())).build();
                    projectDetails.add(build);
                } catch (IOException e) {
                    log.error("Exception while getting Branches from GitHub " + e.getLocalizedMessage(), e);
                    throw new ThrowException(e);
                }
            });
            listMap.put("branches", projectDetails);
        } catch (IOException e) {
            log.error("Exception while getting projects from GitHub " + e.getLocalizedMessage(), e);
            throw new ThrowException(e);
        }
        log.info("project details: " + listMap);
        return listMap;
    }

    @Override
    public ResponseEntity testConnection(String baseURI, String username, String token) {
        log.info("testing connection of GitHub");
        try {
            HttpStatus status;
            String statusMessage;
            String message;
            GitHub gitHub = GitHub.connectUsingOAuth(baseURI, token);
            GHMyself myself = gitHub.getMyself();
            if (myself.getLogin().equals(username)) {
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
        } catch (IOException e) {
            log.error("Exception while Testing Connection for GitHub " + e.getLocalizedMessage(), e);
            throw new ThrowException(e);
        }
    }
}
