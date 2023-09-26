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

import com.cognizant.workbench.base.configurations.CustomBasicAuthentication;
import com.cognizant.workbench.base.models.LeapApiResponse;
import com.cognizant.workbench.clients.BitBucketClient;
import com.cognizant.workbench.vcs.model.bitbucket.BitBucketProjectDetails;
import com.cognizant.workbench.vcs.model.bitbucket.Clone;
import com.cognizant.workbench.vcs.model.bitbucket.branch.BitBucketBranch;
import com.cognizant.workbench.vcs.model.bitbucket.branch.BitBucketBranchesDetails;
import com.cognizant.workbench.vcs.model.bitbucket.team.Team;
import com.cognizant.workbench.vcs.model.bitbucket.team.TeamDetails;
import com.cognizant.workbench.vcs.model.common.GitProjectDetails;
import com.cognizant.workbench.vcs.model.common.SCMConstants;
import com.cognizant.workbench.vcs.model.common.SCMDetails;
import feign.FeignException;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 9/20/2019 4:31 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class BitBucketService implements ISCMService {

    private static final String CREATED_TEMPLATE = "File created/updated successfully in BitBucket.";
    private BitBucketClient client;
    private CustomBasicAuthentication authentication;
    private SCMCommonService scmCommonService;

    @Override
    public ResponseEntity pushJenkinsFile(String projectId) {
        log.info("Getting Project and pipeline script based on project id");
        Map<String, Object> map = scmCommonService.validateAndReturnPipeline(projectId, SCMConstants.BITBUCKET);
        return pushFileToSCM((SCMDetails) map.get(SCMConstants.SCM), (String) map.get(SCMConstants.PIPELINE));
    }

    @Override
    public Map<String, List> getProjectDetails(String baseURI, String username, String token) {
        log.info("Getting project and branch details");
        Map<String, List> listMap = new HashMap<>();
        List<GitProjectDetails> projectDetails = new ArrayList<>();
        Map<String, String> requestHeader = authentication.getBasicAuthentication(username, token);
        List<String> usernameList = new ArrayList<>();
        usernameList.add(username);
        TeamDetails teamDetails = client.getTeams(URI.create(baseURI), "member", requestHeader);
        usernameList.addAll(teamDetails.getValues().stream().map(Team::getUsername).collect(Collectors.toList()));

        List<String> projectNames = new ArrayList<>();
        usernameList.forEach(owner -> {
            BitBucketProjectDetails projects = client.getProjects(URI.create(baseURI), owner, requestHeader);
            projects.getValues().forEach(bitBucketProject -> {
                String projectFullName = bitBucketProject.getFullName();
                String cloneRepoUrl = bitBucketProject.getLinks().getClone().stream().filter(clone -> clone.getName().equalsIgnoreCase("https"))
                        .map(Clone::getHref).collect(Collectors.toList()).get(0);
                String branchesUrl = (String) bitBucketProject.getLinks().getBranches().get("href");
                BitBucketBranchesDetails branches = client.getBranches(URI.create(branchesUrl), requestHeader);
                List<String> branchList = branches.getValues().stream().map(BitBucketBranch::getName).collect(Collectors.toList());
                GitProjectDetails gitProjectDetails = GitProjectDetails.builder().projectName(projectFullName).repoUrl(cloneRepoUrl).branches(branchList).build();
                projectDetails.add(gitProjectDetails);
                projectNames.add(projectFullName);
            });
        });
        listMap.put("projects", projectNames);
        listMap.put("branches", projectDetails);

        log.info("Project details got from BitBucket: " + listMap);

        return listMap;
    }

    @Override
    public ResponseEntity testConnection(String baseURI, String username, String token) {
        log.info("Testing connection of BitBucket");
        Map<String, String> requestHeader = authentication.getBasicAuthentication(username, token);
        try (Response response = client.testConnection(URI.create(baseURI), requestHeader)) {
            if (response.status() == HttpStatus.OK.value()) {
                LeapApiResponse apiResponse = new LeapApiResponse(LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Success",
                        SCMConstants.TEST_CONNECTION_SUCCESSFUL
                );
                log.info(SCMConstants.TEST_CONNECTION_SUCCESSFUL);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                log.error("Exception while testing BitBucket connection...!");
                throw FeignException.errorStatus("Test Connection: ", response);
            }
        }
    }

    @Override
    public ResponseEntity pushFileToSCM(SCMDetails details, String pipeline) {
        log.info("Pushing file into repo");
        Map<String, Object> map = new HashMap<>();
        map.put(details.getFileName(), pipeline);
        map.put("branch", details.getBranch());
        map.put("message", details.getCommitMessage());
        Map<String, String> requestHeader = authentication.getBasicAuthentication(details.getUsername(), details.getToken());
        Response response = client.pushFile(URI.create(details.getApiOrHostUrl()), details.getProjectOrRepoName(), map, requestHeader);
        if (response.status() == HttpStatus.CREATED.value() || response.status() == HttpStatus.OK.value()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
            httpHeaders.remove("content-type");
            httpHeaders.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
            LeapApiResponse apiResponse = new LeapApiResponse(LocalDateTime.now(),
                    HttpStatus.OK.value(),
                    "Success",
                    CREATED_TEMPLATE
            );
            log.info(CREATED_TEMPLATE);
            return new ResponseEntity(apiResponse, HttpStatus.OK);
        } else {
            log.error("Exception while pushing file into repo");
            throw FeignException.errorStatus("Error while Creating file: ", response);
        }
    }
}
