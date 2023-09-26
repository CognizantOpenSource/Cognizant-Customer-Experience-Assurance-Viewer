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

package com.cognizant.workbench.clients;

import com.cognizant.workbench.base.services.WhiteListed;
import com.cognizant.workbench.jenkins.model.*;
import feign.*;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by 784420 on 7/5/2019 3:33 PM
 */
@Validated
public interface JenkinsClient {

    @RequestLine("GET /api/json")
    Jenkins getJobList(@WhiteListed URI baseUrl,
                       @HeaderMap Map<String, Object> requestHeader
    );

    @RequestLine("GET /job/{jobName}/api/json")
    JenkinsJobDetails getJob(@WhiteListed URI baseUrl,
                             @HeaderMap Map<String, Object> requestHeader,
                             @Param("jobName") String jobName
    );

    @RequestLine("GET /job/{jobName}/api/json?tree={params}")
    JenkinsJobDetails getJobDetails(@WhiteListed URI baseUrl,
                                    @HeaderMap Map<String, Object> requestHeader,
                                    @Param("jobName") String jobName,
                                    @Param("params") String params
    );

    @RequestLine("GET /api/json?tree={params}")
    JenkinsAllJobDetails getJenkinsAllBuildsReports(@WhiteListed URI strBaseURI,
                                                    @HeaderMap Map<String, Object> requestHeader,
                                                    @Param("params") String params
    );

    @RequestLine("GET /job/{jobName}/{buildId}/wfapi/describe")
    JenkinsBuildDetails getBuildDetails(@WhiteListed URI baseUrl,
                                        @HeaderMap Map<String, Object> requestHeader,
                                        @Param("jobName") String jobName,
                                        @Param("buildId") int buildId
    );

    @RequestLine("GET /blue/rest/organizations/jenkins/pipelines/{jobName}/runs/{buildId}/nodes/")
    List<Stage> getBlueBuildDetails(@WhiteListed URI baseUrl,
                                    @HeaderMap Map<String, Object> requestHeader,
                                    @Param("jobName") String jobName,
                                    @Param("buildId") int buildId
    );

    @RequestLine("GET /job/{jobName}/{buildId}/consoleText")
    Response getBuildLogs(URI strBaseURI,
                          @HeaderMap Map<String, Object> requestHeader,
                          @Param("jobName") String jobName,
                          @Param("buildId") int buildId
    );

    @RequestLine("GET /job/{jobName}/config.xml")
    Response getJenkinsConfigXml(URI strBaseURI,
                                 @HeaderMap Map<String, Object> requestHeader,
                                 @Param("jobName") String jobName
    );

    @RequestLine("GET /job/{jobName}/{buildId}/testReport/api/json")
    JenkinsStageReport getJenkinsStageReport(URI strBaseURI,
                                             @HeaderMap Map<String, Object> requestHeader,
                                             @Param("jobName") String jobName,
                                             @Param("buildId") int buildId
    );

    @RequestLine("GET /crumbIssuer/api/json")
    JenkinsCrumb getCrumb(@WhiteListed URI baseUrl,
                          @HeaderMap Map<String, Object> requestHeader
    );

    @RequestLine("POST /createItem?name={name}")
    @Body("{config}")
    Response createJob(@WhiteListed URI baseUrl,
                       @HeaderMap Map<String, Object> requestHeader,
                       @Param("name") String name,
                       @Param("config") String config
    );

    @RequestLine("POST /job/{jobName}/config.xml")
    @Body("{config}")
    Response updateJob(@WhiteListed URI baseUrl,
                       @HeaderMap Map<String, Object> requestHeader,
                       @Param("jobName") String jobName,
                       @Param("config") String config
    );

    @RequestLine("POST /job/{jobName}/build")
    Response buildJob(@WhiteListed URI baseUrl,
                      @Param("jobName") String jobName,
                      @HeaderMap Map<String, Object> requestHeader
    );

    @RequestLine("POST /job/{jobName}/buildWithParameters")
    Response buildJobWithParams(@WhiteListed URI baseUrl,
                                @Param("jobName") String jobName,
                                @QueryMap Map<String, String> requestParams,
                                @HeaderMap Map<String, Object> requestHeader
    );

    @RequestLine("POST /script")
    @Body("{script}")
    Response buildScript(@WhiteListed URI baseUrl,
                         @HeaderMap Map<String, Object> requestHeader,
                         @Param("script") String script
    );

    @RequestLine("POST /scriptText?script={script}")
    Response buildScriptText(@WhiteListed URI baseUrl,
                             @HeaderMap Map<String, Object> requestHeader,
                             @Param("script") String script
    );

    @RequestLine("POST /job/{jobName}/{buildId}/stop")
    Response stopBuild(@WhiteListed URI baseUrl,
                       @HeaderMap Map<String, Object> requestHeader,
                       @Param("jobName") String jobName,
                       @Param("buildId") int buildId
    );

    @RequestLine("POST /blue/rest/organizations/jenkins/pipelines/{jobName}/runs/{buildId}/replay/")
    Response restartBuild(@WhiteListed URI baseUrl,
                          @HeaderMap Map<String, Object> requestHeader,
                          @Param("jobName") String jobName,
                          @Param("buildId") int buildId
    );

    @RequestLine("GET /blue/rest/organizations/jenkins/pipelines/{jobName}/runs/{buildId}/nodes/{stageId}/steps")
    List<StageStep> getStageStepsList(@WhiteListed URI baseUrl,
                                      @HeaderMap Map<String, Object> requestHeader,
                                      @Param("jobName") String jobName,
                                      @Param("buildId") int buildId,
                                      @Param("stageId") int stageId
    );

    @RequestLine("GET /blue/rest/organizations/jenkins/pipelines/{jobName}/runs/{buildId}/nodes/{stageId}")
    Stage getBlueStageDetails(@WhiteListed URI baseUrl,
                                      @HeaderMap Map<String, Object> requestHeader,
                                      @Param("jobName") String jobName,
                                      @Param("buildId") int buildId,
                                      @Param("stageId") int stageId
    );

    @RequestLine("GET /blue/rest/organizations/jenkins/pipelines/{jobName}/runs/{buildId}/nodes/{stageId}/steps/{stepId}/log")
    Response getStageStepLog(@WhiteListed URI baseUrl,
                             @HeaderMap Map<String, Object> requestHeader,
                             @Param("jobName") String jobName,
                             @Param("buildId") int buildId,
                             @Param("stageId") int stageId,
                             @Param("stepId") int stepId
    );
}
