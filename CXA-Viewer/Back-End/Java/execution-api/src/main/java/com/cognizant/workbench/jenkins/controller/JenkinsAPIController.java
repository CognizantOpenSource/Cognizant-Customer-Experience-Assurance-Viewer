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

package com.cognizant.workbench.jenkins.controller;

import com.cognizant.workbench.jenkins.model.*;
import com.cognizant.workbench.jenkins.service.JenkinsAPIService;
import com.cognizant.workbench.pipeline.service.ProjectService;
import com.cognizant.workbench.pipeline.util.JenkinsUtil;
import com.google.common.html.HtmlEscapers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.min;

/**
 * Created by 784420 on 7/5/2019 3:45 PM
 */
@RestController
@RequestMapping(value = "/jenkins")
@Slf4j
@RequiredArgsConstructor
public class JenkinsAPIController {
    private final JenkinsAPIService jenkinsAPIService;
    private final ProjectService projectService;
    private final JenkinsRequestParam jenkinsRequestParam;
    private final JenkinsUtil jenkinsUtil;

    /**
     * TestConnection with Jenkins based on the Jenkins Url and Credentials, Here Password is token or token
     *
     * @param jenkinsDetails Jenkins Server details
     * @return Test connection status, its valid credentials or not
     */
    @PostMapping(value = "/test")
    @PreAuthorize("hasPermission('Jenkins','jenkins.test')")
    public Jenkins testJenkinsConnection(@RequestBody @Valid JenkinsDetails jenkinsDetails) {
        return jenkinsAPIService.getJenkinsJobList(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails));
    }

    /**
     * Getting all list of jobs from the jenkins server
     *
     * @return Jenkins all jobs list
     */
    @GetMapping(value = "/jobs")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public Jenkins getJenkinsJobs() {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsJobList(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails));
    }

    /**
     * Getting jenkins job build report for all users which are having the access to the logged in user
     *
     * @return Jenkins job build report
     */
    @GetMapping(value = "/jobs/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public List<JenkinsJobDetails> getJenkinsJobsReports() {
        List<String> jobNames = filterJenkinsJobNames();
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jobNames.stream().map(
                jobName -> jenkinsAPIService.getJenkinsJobDetails(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, false))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()
                );
    }

    /**
     * Jenkins all Job's build report
     *
     * @return Jenkins all jobs build report for mentioned projectNames
     */
    @GetMapping(value = "/builds/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public List<Build> getJenkinsAllBuildsReports() {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsAllBuildsReports(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), projectService.getUserProjectJobNames());
    }

    /**
     * Getting Jenkins Job Details
     *
     * @param jobName Jenkins jobName
     * @return Jenkins job report based on {@code jobName} jobName
     */
    @GetMapping(value = "/jobs/{jobName}/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public JenkinsJobDetails getJenkinsJobReport(@NotBlank(message = "JobName in the request should not be empty/null") @PathVariable String jobName) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsJobDetails(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName);
    }

    /**
     * Getting Jenkins Job Details
     *
     * @param jobName Jenkins jobName
     * @return Builds report for 20 builds
     */
    @GetMapping(value = "/jobs/{jobName}/builds/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public List<JenkinsBuildDetails> getJenkinsJobBuildsReport(
            @NotBlank(message = "JobName in the request should not be empty/null") @PathVariable String jobName) {
        String escapeJobName = HtmlEscapers.htmlEscaper().escape(jobName);
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        JenkinsJobDetails jenkinsJobDetails = jenkinsAPIService.getJenkinsJobDetails(
                jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), escapeJobName);
        List<Build> builds = jenkinsJobDetails.getBuilds();
        builds = builds.subList(0, min(builds.size(), 20));
        List<String> ids = builds.stream().map(Build::getId).collect(Collectors.toList());
        return ids.stream().map(Integer::parseInt).map(id ->
                jenkinsAPIService.getJenkinsBuildDetails(
                        jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), escapeJobName, id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * Getting Jenkins Job Build Details
     *
     * @param jobName Jenkins jobName
     * @param buildId Jenkins job's Build Id
     * @return Build report based on provided {@code jobName} jobName and {@code buildId} buildId
     */
    @Validated
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public JenkinsBuildDetails getJenkinsBuildReport(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                             String jobName,
                                                     @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                                     @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                             int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsBuildDetails(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * Getting Jenkins Build log
     *
     * @param jobName Jenkins jobName
     * @param buildId Jenkins job's Build Id
     * @return Build logs based on provided {@code jobName} jobName and {@code buildId} buildId
     */
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/report/log", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public String getJenkinsBuildLog(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                             String jobName,
                                     @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                     @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                             int buildId) {
        String escapeJobName = HtmlEscapers.htmlEscaper().escape(jobName);
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsBuildLogs(
                jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), escapeJobName, buildId);
    }

    /**
     * Getting Jenkins Job Config xml data
     *
     * @param jobName Jenkins jobName
     * @return Build report based on provided {@code jobName} jobName
     */
    @Validated
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/report/configScript")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public String getJenkinsConfig(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                           String jobName) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return "";
    }


    /**
     * Getting stage wise test cases report (Passed, Failed, Skipped count and percentage)
     *
     * @param jobName Jenkins jobName
     * @param buildId Jenkins job's Build Id
     * @return TestCase Report stage wise, based on provided {@code jobName} jobName and {@code buildId} buildId
     */
    @Validated
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/stages/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public Map<String, TestReport> getJenkinsStageReport(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                                 String jobName,
                                                         @PathVariable
                                                         @NotBlank(message = "BuildId/Number in the request should not be empty/null") @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative") int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsStageReport(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * Getting TestReport for the particular build and based on job
     *
     * @param jobName Name of the Job which should get from the Jenkins server
     * @param buildId Build Id
     * @return TestReport based on provided {@code jobName} jobName and {@code buildId} buildId
     */
    @Validated
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/testReport")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public JenkinsStageReport getJenkinsBuildTestReport(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                                String jobName,
                                                        @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                                        @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                                int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsBuildTestReport(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * Getting all reports for that job and last 10 builds, Getting SummaryStatistics of min, max, average of duration, Today's/Total builds and status wise build count
     *
     * @param jobName Name of the Job which should get from the Jenkins server
     * @return All reports based on provided {@code jobName} jobName
     */
    @GetMapping(value = "/jobs/{jobName}/analysis/report")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public JenkinsAnalysisReport getJenkinsJobAnalysisReport(
            @NotBlank(message = "JobName in the request should not be empty/null") @PathVariable String jobName,
            @Nullable @RequestParam(name = "buildId", value = "") String buildId) {
        String escapeJobName = HtmlEscapers.htmlEscaper().escape(jobName);
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        JenkinsJobDetails jenkinsJobDetails = jenkinsAPIService.getJenkinsJobDetails(
                jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), escapeJobName);
        List<Build> builds = jenkinsJobDetails.getBuilds();

        JenkinsAnalysisReport jenkinsAnalysisReport = jenkinsUtil.parseBuilds(builds);

        if (!StringUtils.isEmpty(buildId)) {
            Optional<Build> optional = builds.stream().filter(build -> build.getId().equalsIgnoreCase(buildId)).findFirst();
            if (optional.isPresent()) {
                int i = builds.indexOf(optional.get());
                i = Math.max(i, 0);
                int size = builds.size();
                if (size <= 10) {
                    builds = builds.subList(0, size);
                } else if ((size - i) <= 10) {
                    builds = builds.subList(size - 10, size);
                } else {
                    builds = builds.subList(i, min(builds.size(), i + 10));
                }
            } else {
                builds = builds.subList(0, min(builds.size(), 10));
            }
        } else {
            builds = builds.subList(0, min(builds.size(), 10));
        }
        List<String> ids = builds.stream().map(Build::getId).collect(Collectors.toList());
        List<JenkinsBuildDetails> jenkinsBuildDetailsList = ids.stream().map(Integer::parseInt)
                .map(id -> jenkinsAPIService.getJenkinsBuildDetails(
                        jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), escapeJobName, id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        jenkinsAnalysisReport.setJobId(jenkinsJobDetails.getName());
        jenkinsAnalysisReport.setBuilds(jenkinsBuildDetailsList);
        return jenkinsAnalysisReport;
    }

    /**
     * Getting Jenkins Job Config xml data
     *
     * @param jobName Jenkins jobName
     * @param buildId Build Id
     * @return config xml data (pipeline script) based on provided {@code jobName} jobName and {@code buildId} buildId
     */
    @Validated
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/report/config")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public ResponseEntity getJenkinsConfigScript(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                         String jobName,
                                                 @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                                 @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                         int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getJenkinsConfigScript(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * @param map Key and value pairs of environment variables which have to set in Jenkins
     * @return response message
     */
    @Validated
    @PostMapping(value = "/script/env")
    @PreAuthorize("hasPermission('Jenkins','jenkins.env.update')")
    public ResponseEntity updateEnvironmentVariables(@RequestBody @Valid
                                                     @NotEmpty(message = "Map Should not be Empty/Null")
                                                     @Size(min = 1, message = "Map should contain at least one key and value pair")
                                                             Map<String, Object> map) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.updateEnvironmentVariables(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), map);
    }

    /**
     * @param key Environment key which should remove from jenkins
     * @return response message
     */
    @Validated
    @DeleteMapping(value = "/script/env")
    @PreAuthorize("hasPermission('Jenkins','jenkins.env.delete')")
    public ResponseEntity removeEnvironmentVariable(@RequestParam
                                                    @NotBlank(message = "Key should not be blank or empty")
                                                            String key) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.removeEnvironmentVariable(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), key);
    }

    /**
     * Stopping build based on the {@code jobName} and {@code buildId}
     *
     * @param jobName Jenkins jobName
     * @param buildId Build Id
     * @return response message
     */
    @PostMapping(value = "/jobs/{jobName}/builds/{buildId}/stop")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.build')")
    public ResponseEntity<Object> stopBuild(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                    String jobName,
                                            @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                            @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                    int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.stopBuild(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * Restarting build based on the {@code jobName} and {@code buildId}
     *
     * @param jobName Jenkins jobName
     * @param buildId Build Id
     * @return response message
     */
    @PostMapping(value = "/jobs/{jobName}/builds/{buildId}/restart")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.build')")
    public ResponseEntity restartBuild(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                               String jobName,
                                       @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                       @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                               int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.restartBuild(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * Getting Stage wise Step Details based on the {@code jobName} , {@code buildId} and {@code stageId}
     *
     * @param jobName Jenkins jobName
     * @param buildId Build Id
     * @param stageId Stage Id
     * @return List of stages for the job based on the {@code jobName} , {@code buildId} and {@code stageId}
     */
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/stages/{stageId}")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public List<StageStep> getStageStepsList(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                     String jobName,
                                             @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                             @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                     int buildId,
                                             @PathVariable @NotBlank(message = "Stage Id/Number in the request should not be empty/null")
                                             @Min(value = 1, message = "Stage Id/Number in the request should not be Zero/Negative")
                                                     int stageId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getStageStepsList(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId, stageId);
    }

    /**
     * Getting step log based on the {@code jobName} , {@code buildId} and {@code stageId}
     *
     * @param jobName Jenkins jobName
     * @param buildId Build Id
     * @param stageId Stage Id
     * @return List of step wise logs
     */
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/stages/{stageId}/log")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public List<StageStepLog> getStageStepsLog(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                       String jobName,
                                               @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                               @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                       int buildId,
                                               @PathVariable @NotBlank(message = "Stage Id/Number in the request should not be empty/null")
                                               @Min(value = 1, message = "Stage Id/Number in the request should not be Zero/Negative")
                                                       int stageId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getStageStepsLog(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId, stageId);
    }

    /**
     * Getting step log based on the {@code jobName} and {@code buildId}
     *
     * @param jobName Jenkins jobName
     * @param buildId Build Id
     * @return List of stage wise logs
     */
    @GetMapping(value = "/jobs/{jobName}/builds/{buildId}/stages/log")
    @PreAuthorize("hasPermission('Jenkins','jenkins.job.read')")
    public Map<String, Object> getStagesLog(@PathVariable @NotBlank(message = "JobName in the request should not be empty/null")
                                                    String jobName,
                                            @PathVariable @NotBlank(message = "BuildId/Number in the request should not be empty/null")
                                            @Min(value = 1, message = "BuildId/Number in the request should not be Zero/Negative")
                                                    int buildId) {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getStagesLog(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails), jobName, buildId);
    }

    /**
     * @return All Global Environment variables of jenkins server
     */
    @GetMapping(value = "/script/allEnvVars")
    @PreAuthorize("hasPermission('Jenkins','jenkins.env.read')")
    public ResponseEntity getGlobalEnvironmentVariables() {
        JenkinsDetails jenkinsDetails = jenkinsRequestParam.getUserJenkinsDetails();
        return jenkinsAPIService.getGlobalEnvironmentVariables(jenkinsRequestParam.getBaseURI(jenkinsDetails), jenkinsRequestParam.getRequestHeader(jenkinsDetails));
    }

    private List<String> filterJenkinsJobNames() {
        List<String> projectJobNames = projectService.getUserProjectJobNames();
        Jenkins jenkinsJobs = getJenkinsJobs();
        return jenkinsJobs.getJobs().stream().filter(job -> projectJobNames.contains(job.getName())).map(Job::getName).collect(Collectors.toList());
    }

}
