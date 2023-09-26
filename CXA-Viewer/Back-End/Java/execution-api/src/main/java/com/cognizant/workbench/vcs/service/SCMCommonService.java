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

import com.cognizant.workbench.base.models.UserPrincipal;
import com.cognizant.workbench.error.ProjectNotFoundException;
import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.pipeline.model.Project;
import com.cognizant.workbench.pipeline.pipelineconverter.PipelineString;
import com.cognizant.workbench.pipeline.service.PipelineService;
import com.cognizant.workbench.pipeline.service.ProjectService;
import com.cognizant.workbench.user.model.UserSettings;
import com.cognizant.workbench.user.service.UserSettingsService;
import com.cognizant.workbench.vcs.model.common.SCMConstants;
import com.cognizant.workbench.vcs.model.common.SCMCredDetails;
import com.cognizant.workbench.vcs.model.common.SCMDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 784420 on 9/27/2019 4:09 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class SCMCommonService {

    private ProjectService projectService;
    private PipelineService pipelineService;
    private UserSettingsService userSettingsService;

    public Map<String, Object> validateAndReturnPipeline(String projectId, String scmType) {
        log.info("Getting Project and pipeline script based on project id");
        Project project = projectService.getProject(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
        PipelineString pipelineString = pipelineService.generatePipeline(project);

        Map<String, String> links = Objects.requireNonNull(project.getLinks(), String.format(SCMConstants.REPOSITORY_BRANCH_DETAILS_ARE_NOT_FOUND_IN_PROJECT, project.getName()));
        SCMDetails details = generateSCMDetails(
                Objects.requireNonNull(links.get("repo"), String.format(SCMConstants.REPOSITORY_DETAILS_ARE_NOT_FOUND_IN_PROJECT, project.getName())),
                Objects.requireNonNull(links.get("branch"), String.format(SCMConstants.BRANCH_DETAILS_ARE_NOT_FOUND_IN_THE_PROJECT, project.getName())),
                scmType
        );
        String pipeline = pipelineString.getPipeline();

        Map<String, Object> map = new HashMap<>();
        map.put(SCMConstants.SCM, details);
        map.put(SCMConstants.PIPELINE, pipeline);
        return map;
    }

    private SCMDetails generateSCMDetails(String projectOrRepoName, String branch, String scmType) {
        log.info("Getting SCM Details from user settings. SCM is: "+scmType);
        SCMCredDetails scmCredDetails = getSCMCredDetails(scmType);
        return SCMDetails.builder().apiOrHostUrl(scmCredDetails.getApiOrHostUrl())
                .username(scmCredDetails.getUsername())
                .token(scmCredDetails.getToken())
                .projectOrRepoName(projectOrRepoName)
                .branch(branch)
                .fileName(SCMConstants.JENKINSFILE)
                .commitMessage(SCMConstants.COMMIT_MESSAGE)
                .build();
    }

    public SCMCredDetails getSCMCredDetails(String scmType) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserSettings userSettings = userSettingsService.getUserSettings(userPrincipal.getEmail());
        SCMCredDetails scmCredDetails = null;
        try {
            switch (scmType.toLowerCase(Locale.ENGLISH)) {
                case SCMConstants.GITLAB:
                    scmCredDetails = userSettings.getGitlab();
                    break;
                case SCMConstants.GITHUB:
                    scmCredDetails = userSettings.getGithub();
                    break;
                case SCMConstants.BITBUCKET:
                    scmCredDetails = userSettings.getBitbucket();
                    break;
                default:
                    throw new ThrowException(String.format(SCMConstants.PROVIDED_SCM_TYPE_S_IS_INVALID, scmType));
            }
        } catch (NullPointerException e) {
            log.error("Exception while fetching SCM details");
        }
        if (null == scmCredDetails) throw new ThrowException(String.format(SCMConstants.PLEASE_UPDATE_USER_SETTINGS, scmType));
        if (StringUtils.isEmpty(scmCredDetails.getApiOrHostUrl()) || StringUtils.isEmpty(scmCredDetails.getUsername()) || StringUtils.isEmpty(scmCredDetails.getToken())) {
            throw new ThrowException(String.format(SCMConstants.PLEASE_UPDATE_USER_SETTINGS, scmType));
        }
        return scmCredDetails;
    }
}
