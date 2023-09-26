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

package com.cognizant.workbench.jenkins.service;

import com.cognizant.workbench.base.models.LeapApiResponse;
import com.cognizant.workbench.clients.JenkinsClient;
import com.cognizant.workbench.error.ProjectNotFoundException;
import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.jenkins.model.*;
import com.cognizant.workbench.pipeline.model.Constants;
import com.cognizant.workbench.pipeline.model.PipelineConfigBean;
import com.cognizant.workbench.pipeline.model.Project;
import com.cognizant.workbench.pipeline.pipelineconverter.ParameterConverter;
import com.cognizant.workbench.pipeline.pipelineconverter.PipelineString;
import com.cognizant.workbench.pipeline.service.PipelineService;
import com.cognizant.workbench.pipeline.service.ProjectService;
import com.cognizant.workbench.pipeline.util.JenkinsPath;
import com.cognizant.workbench.pipeline.util.JenkinsUtil;
import com.cognizant.workbench.pipeline.util.Util;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 7/9/2019 3:02 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JenkinsAPIService {

    private static final String JENKINS_JOB_DETAILS_PARAMS = "name,builds[id,result,duration,timestamp]";
    private static final String JENKINS_ALL_BUILDS_DETAILS_PARAMS = "jobs[name,builds[id,result,duration,timestamp,fullDisplayName,displayName]]";
    private static final String LOG_TEMPLATE_DATA_IN_DB = "Jenkins Data present in local DB, returning available data";
    private static final String LOG_TEMPLATE_DATA_NOT_IN_DB = "Jenkins Data not available in local DB, Getting from Jenkins server and returning";

    private static final String PIPELINE_SCRIPT = "##PipelineScript##";
    private static final String JOB_NAME = "##jobName##";
    private static final String BUILD_ID = "##buildId##";
    private static final String JENKINS_JOB_URL = "%s/job/%s/";
    private static final String ENV_VAR_CREATED = "Jenkins Environmental Variable are Updated Successfully.";
    private static final String ENV_VAR_REMOVED = "Jenkins Environmental Variable are Removed Successfully.";
    private static final String JOB_STOPPED = "%s Build of %s Job Stopped Successfully.";
    private static final String JOB_RESTARTED = "%s Build of %s Job Restarted Successfully.";
    private static final String JOB_CREATED = "%s Job Created Successfully.";
    private static final String JOB_UPDATED = "%s Job Updated Successfully.";
    private static final String JOB_BUILD = "%s Job Requested for Build Successfully.";
    private static final String LOG_TEMPLATE = "JobName: %s :BaseURI: %s";
    private static final String LOG_TEMPLATE_START = "Starts:";
    private static final String LOG_RESPONSE_TEMPLATE = " body: %s %n header: %s %n status: %s %n toString(): %s ";
    private static final Map<String, String> map = Collections.singletonMap("Content-Type", "application/xml");

    private final ProjectService projectService;
    private final PipelineService pipelineService;
    private final JenkinsClient jenkinsClient;
    private final JenkinsUtil jenkinsUtil;
    private final JenkinsDataService jenkinsDataService;
    private final JenkinsPath path;

    /**
     * Getting Job list from Jenkins
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @return list of jobs which are available in Jenkins server .
     */
    public Jenkins getJenkinsJobList(URI baseUri, Map<String, Object> requestHeader) {
        return jenkinsClient.getJobList(baseUri, requestHeader);
    }

    /**
     * Getting Job from Jenkins based on ProjectId
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param projectId     Project id - Unique id for project
     * @return Job details from Jenkins server for those projects if exists
     */
    public JenkinsJobDetails getJenkinsJob(URI baseUri, Map<String, Object> requestHeader, String projectId) {
        log.info(LOG_TEMPLATE_START);

        /*Getting project Details from DB*/
        Project project = getProjectById(projectId);
        String projectName = project.getName();
        log.info(String.format(LOG_TEMPLATE, projectName, baseUri));

        return jenkinsClient.getJob(baseUri, requestHeader, projectName);
    }

    /**
     * Getting full job details from Jenkins based on JobName
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins JobName
     * @return job full details
     */
    public JenkinsJobDetails getJenkinsJobDetails(URI baseUri, Map<String, Object> requestHeader, String jobName) {
        return getJenkinsJobDetails(baseUri, requestHeader, jobName, true);
    }

    /**
     * Getting full job details from Jenkins based on JobName
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param throwError    boolean value of throwing error or not if any exception while execution
     * @return job full details
     */
    public JenkinsJobDetails getJenkinsJobDetails(URI baseUri, Map<String, Object> requestHeader, String jobName, boolean throwError) {
        log.info(" Starts: " + String.format(LOG_TEMPLATE, jobName, baseUri));
        try {
            return jenkinsClient.getJobDetails(baseUri, requestHeader, jobName, JENKINS_JOB_DETAILS_PARAMS);
        } catch (FeignException e) {
            if (throwError)
                throw e;
            else
                return null;
        }
    }

    /**
     * Jenkins all Job's build report
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param projectNames  List of job names which are get from the jenkins report
     * @return Jenkins all jobs build report for mentioned projectNames
     */
    public List<Build> getJenkinsAllBuildsReports(URI baseUri, Map<String, Object> requestHeader, List<String> projectNames) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, projectNames, baseUri));
        JenkinsAllJobDetails jobDetails = jenkinsClient.getJenkinsAllBuildsReports(baseUri, requestHeader, JENKINS_ALL_BUILDS_DETAILS_PARAMS);
        List<Build> builds = new ArrayList<>();

        jobDetails.getJobs().stream().filter(job -> projectNames.contains(job.getName()))
                .forEach(jenkinsJobDetails -> {
                            jenkinsJobDetails.getBuilds().forEach(
                                    build -> build.setJobName(jenkinsJobDetails.getName())
                            );
                            builds.addAll(jenkinsJobDetails.getBuilds());
                        }
                );

        builds.sort(Collections.reverseOrder());
        return builds;
    }

    /**
     * Jenkins Build details for mentioned Job and id
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return build details for mentioned build id
     */
    public JenkinsBuildDetails getJenkinsBuildDetails(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseUri));
        Optional<ApiCache> optional = jenkinsDataService.getJenkinsData(baseUri, jobName, buildId, path.getReportPath());
        JenkinsBuildDetails buildDetails;

        if (optional.isPresent()) {
            log.info(LOG_TEMPLATE_DATA_IN_DB);
            buildDetails = (JenkinsBuildDetails) optional.get().getData();
        } else {
            log.info(LOG_TEMPLATE_DATA_NOT_IN_DB);
            buildDetails = jenkinsDataService.insertJenkinsData(baseUri, requestHeader, jobName, buildId);
            jenkinsDataService.saveJenkinsLogData(baseUri, requestHeader, jobName, buildId);
            jenkinsDataService.saveJenkinsTestReportData(baseUri, requestHeader, jobName, buildId);
        }
        return buildDetails;
    }

    /**
     * Getting logs for the mentioned Jenkins Job build
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return log for the mentioned build id and job
     */
    public String getJenkinsBuildLogs(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseUri));

        Optional<ApiCache> optional = jenkinsDataService.getJenkinsData(baseUri, jobName, buildId, path.getLogPath());
        String strResponse = "";
        if (optional.isPresent()) {
            log.info(LOG_TEMPLATE_DATA_IN_DB);
            strResponse = (String) optional.get().getData();
        } else {
            log.info(LOG_TEMPLATE_DATA_NOT_IN_DB);
            Response response = jenkinsClient.getBuildLogs(baseUri, requestHeader, jobName, buildId);
            feign.codec.Decoder dc = new Decoder.Default();
            try {
                strResponse = (String) dc.decode(response, String.class);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        return strResponse;
    }

    /**
     * Getting configuration for the Job
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @return config xml
     */
    public String getJenkinsConfigXml(URI baseUri, Map<String, Object> requestHeader, String jobName) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseUri));
        return jenkinsUtil.xmlTagFromResponse(
                jenkinsClient.getJenkinsConfigXml(baseUri, requestHeader, jobName)
        );
    }

    /**
     * Getting test report stage wise count
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return testcase count, passed, failed, skipped and pass percentage
     */
    public Map<String, TestReport> getJenkinsStageReport(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseUri));
        JenkinsStageReport jenkinsStageReport = getJenkinsBuildTestReport(baseUri, requestHeader, jobName, buildId);
        return jenkinsUtil.parseJenkinsStageReport(jenkinsStageReport);
    }

    /**
     * Getting test report for the build
     *
     * @param baseURI       Jenkins server url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return Jenkins test report
     */
    public JenkinsStageReport getJenkinsBuildTestReport(URI baseURI, Map<String, Object> requestHeader, String jobName, int buildId) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseURI));

        Optional<ApiCache> optional = jenkinsDataService.getJenkinsData(baseURI, jobName, buildId, path.getTestReportPath());
        JenkinsStageReport jenkinsStageReport;
        if (optional.isPresent()) {
            log.info(LOG_TEMPLATE_DATA_IN_DB);
            jenkinsStageReport = (JenkinsStageReport) optional.get().getData();
        } else {
            log.info(LOG_TEMPLATE_DATA_NOT_IN_DB);
            jenkinsStageReport = jenkinsClient.getJenkinsStageReport(baseURI, requestHeader, jobName, buildId);
        }

        return jenkinsStageReport;
    }

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param map           Key and value pairs of Environment variables which going to set
     * @return Creation message
     */
    public ResponseEntity<Object> updateEnvironmentVariables(URI baseUri, Map<String, Object> requestHeader, Map<String, Object> map) {
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;
        String script = Util.generateJenkinsScript(map);
        return runScript(baseUri, requestHeader, script, HttpStatus.OK, ENV_VAR_CREATED);
    }

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param envKey        Environment variable to remove from list
     * @return removed message
     */
    public ResponseEntity removeEnvironmentVariable(URI baseUri, Map<String, Object> requestHeader, String envKey) {
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;
        String script = Util.genJenkinsEnvRemoveScript(envKey);
        log.info("Environmental remove script: " + script);
        return runScript(baseUri, requestHeader, script, HttpStatus.NO_CONTENT, ENV_VAR_REMOVED);
    }

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @return Jenkins crumb value
     */
    private ResponseEntity<Object> getJenkinsCrumb(URI baseUri, Map<String, Object> requestHeader) {
        log.info(LOG_TEMPLATE_START);
        JenkinsCrumb jenkinsCrumb;
        ResponseEntity<Object> responseEntity;
        requestHeader.putAll(map);
        try {
            jenkinsCrumb = jenkinsClient.getCrumb(baseUri, requestHeader);
            responseEntity = ResponseEntity.status(HttpStatus.OK.value())
                    .body(jenkinsCrumb);
//            log.info("Crumb is: " + jenkinsCrumb.getCrumb());
        } catch (FeignException e) {
            log.error("Exception while getting Jenkins Crumb: ", e);
            throw e;
        }

        return responseEntity;
    }


    /*Creating the Jenkins JOB using config.xml with Project Id*/

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param projectId     based on which project need to create job, those project Id
     * @return Creation message
     */
    public ResponseEntity<Object> createJenkinsJob(URI baseUri, Map<String, Object> requestHeader, String projectId) {
        log.info(LOG_TEMPLATE_START);
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;

        /*Getting project Details from DB*/
        Project project = getProjectById(projectId);

        /*Generate pipeline script and inserting into config.xml*/
        String configXML = generatePipelineConfig(project);
        String projectName = project.getName();
        String jenkinsJobUrl = String.format(JENKINS_JOB_URL, baseUri, projectName);
        if (!CollectionUtils.isEmpty(project.getCi().getPipeline().getParameters())){
            String parameterXML = new ParameterConverter().xmlConverter(project.getCi().getPipeline().getParameters());
            configXML = Util.getUpdatedConfigXmlValue(parameterXML, configXML, Constants.PROPERTIES_TAG);
        }
        log.info("configXml: " + configXML);

        requestHeader.putAll(map);
        try (Response response = jenkinsClient.createJob(baseUri, requestHeader, projectName, configXML)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));

            if (HttpStatus.OK.value() == response.status()) {
                updatePipelineProject(project, jenkinsJobUrl);
                HttpStatus status = HttpStatus.CREATED;
                JenkinsResponse jenkinsResponse = new JenkinsResponse(LocalDateTime.now(),
                        status.value(),
                        status.toString(),
                        String.format(JOB_CREATED, projectName),
                        response.request().url());
                return ResponseEntity.status(status)
                        .body(jenkinsResponse);
            } else {
                throw FeignException.errorStatus("JenkinsCreateJob", response);
            }
        } catch (FeignException e) {
            log.error("Exception while Creating Jenkins Job: ", e);
            throw e;
        }
    }

    /**
     * Update the Jenkins JOB using config.xml with Project Id
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param projectId     based on which project need to update job, those project Id
     * @return Response message
     */
    public ResponseEntity<Object> updateJenkinsJob(URI baseUri, Map<String, Object> requestHeader, String projectId) {
        log.info(LOG_TEMPLATE_START);

        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;

        /*Getting project Details from DB*/
        Project project = getProjectById(projectId);

        /*Generate pipeline script and inserting into config.xml*/
        String projectName = project.getName();
        String jenkinsRawConfigXml = getJenkinsRawConfigXml(baseUri, requestHeader, projectName);
        String updatedConfigXml = updatePipelineScriptInConfig(project, jenkinsRawConfigXml);
        log.info("configXml " + updatedConfigXml);

        requestHeader.putAll(map);
        try (Response response = jenkinsClient.updateJob(baseUri, requestHeader, projectName, updatedConfigXml)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));

            if (HttpStatus.OK.value() == response.status()) {
                HttpStatus status = HttpStatus.OK;
                JenkinsResponse jenkinsResponse = new JenkinsResponse(LocalDateTime.now(),
                        status.value(),
                        status.toString(),
                        String.format(JOB_UPDATED, projectName),
                        response.request().url());
                return ResponseEntity.status(status)
                        .body(jenkinsResponse);
            } else {
                throw FeignException.errorStatus("JenkinsUpdateJob", response);
            }
        } catch (FeignException e) {
            log.error(" Exception while Creating Jenkins Job: ", e);
            throw e;
        }
    }

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param projectId     based on which project need to build job, those project Id
     * @return Jenkins build response
     */
    public ResponseEntity buildJenkinsJob(URI baseUri, Map<String, Object> requestHeader, String projectId) {
        log.info(LOG_TEMPLATE_START);
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;

        Project project = getProjectById(projectId);
        String projectName = project.getName();

        requestHeader.putAll(map);
        try (Response response = jenkinsClient.buildJob(baseUri, projectName, requestHeader)) {
//            log.info(String.format(LOG_RESPONSE_TEMPLATE, response.body(), response.headers(), response.status(), response.toString()));

            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));

            if (HttpStatus.OK.value() == response.status() || HttpStatus.CREATED.value() == response.status()) {
                HttpStatus status = HttpStatus.CREATED;
                JenkinsResponse jenkinsResponse = new JenkinsResponse(LocalDateTime.now(),
                        status.value(),
                        status.toString(),
                        String.format(JOB_BUILD, projectName),
                        response.request().url());
                return ResponseEntity.status(status)
                        .body(jenkinsResponse);
            } else {
                throw FeignException.errorStatus("JenkinsBuildJob", response);
            }
        }
    }

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param projectId     based on which project need to build job, those project Id
     * @param requestParams Parameter values to build the jenkins job.
     * @return Jenkins build response
     */
    public ResponseEntity buildJobWithParams(URI baseUri, Map<String, Object> requestHeader, String projectId, Map<String, String> requestParams) {
        log.info(LOG_TEMPLATE_START);
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;

        Project project = getProjectById(projectId);
        String projectName = project.getName();

        requestHeader.putAll(map);
        try (Response response = jenkinsClient.buildJobWithParams(baseUri, projectName, requestParams, requestHeader)) {
//            log.info(String.format(LOG_RESPONSE_TEMPLATE, response.body(), response.headers(), response.status(), response.toString()));

            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));

            if (HttpStatus.OK.value() == response.status() || HttpStatus.CREATED.value() == response.status()) {
                HttpStatus status = HttpStatus.CREATED;
                JenkinsResponse jenkinsResponse = new JenkinsResponse(LocalDateTime.now(),
                        status.value(),
                        status.toString(),
                        String.format(JOB_BUILD, projectName),
                        response.request().url());
                return ResponseEntity.status(status)
                        .body(jenkinsResponse);
            } else {
                throw FeignException.errorStatus("JenkinsBuildJob", response);
            }
        }
    }

    /**
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return pipeline script
     */
    public ResponseEntity getJenkinsConfigScript(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseUri));
        String strResponse = "";
        Optional<ApiCache> optional = jenkinsDataService.getJenkinsData(baseUri, jobName, buildId, path.getConfigPath());
        if (optional.isPresent()) {
            log.info(LOG_TEMPLATE_DATA_IN_DB);
            strResponse = (String) optional.get().getData();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(strResponse);
        } else {
            log.info(LOG_TEMPLATE_DATA_NOT_IN_DB);
            /*Getting Jenkins Crumb and adding to RequestHeader*/
            Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
            if (object instanceof Map)
                requestHeader = (Map<String, Object>) object;
            else
                return (ResponseEntity) object;
            String script = JenkinsScriptConstants.JENKINS_CONFIG_SCRIPT;
            script = script.replace(JOB_NAME, jobName).replace(BUILD_ID, String.valueOf(buildId));
            log.info("script: " + script);
            Response response = jenkinsClient.buildScriptText(baseUri, requestHeader, script);
            feign.codec.Decoder dc = new Decoder.Default();
            try {
                strResponse = (String) dc.decode(response, String.class);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
            if (strResponse.contains("pipeline{")) {
                HttpHeaders httpHeaders = new HttpHeaders();
                jenkinsDataService.saveJenkinsConfigData(baseUri, jobName, buildId, strResponse, path.getConfigPath());
                response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
                return ResponseEntity.status(response.status())
                        .headers(httpHeaders)
                        .body(strResponse);
            } else {
                throw new ThrowException("Error Reading Config Script: " + strResponse);
            }
        }
    }


    /**
     * Stopping the jenkins build based on the {@code jobName} and {@code buildId}
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return pipeline script
     */
    public ResponseEntity<Object> stopBuild(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;
        requestHeader.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Response response = jenkinsClient.stopBuild(baseUri, requestHeader, jobName, buildId);
        if (response.status() == HttpStatus.OK.value()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
            httpHeaders.remove("content-type");
            httpHeaders.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
            LeapApiResponse apiResponse = new LeapApiResponse(LocalDateTime.now(),
                    HttpStatus.OK.value(),
                    "Success",
                    String.format(JOB_STOPPED, buildId, jobName)
            );
            return ResponseEntity.status(response.status())
                    .headers(httpHeaders)
                    .body(apiResponse);
        } else {
            throw FeignException.errorStatus("Error while stopping build: ", response);
        }
    }

    /**
     * Restarting the jenkins build based on the {@code jobName} and {@code buildId}
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return pipeline script
     */
    public ResponseEntity restartBuild(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map)
            requestHeader = (Map<String, Object>) object;
        else
            return (ResponseEntity) object;
        requestHeader.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Response response = jenkinsClient.restartBuild(baseUri, requestHeader, jobName, buildId);
        if (response.status() == HttpStatus.OK.value()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
            LeapApiResponse apiResponse = new LeapApiResponse(LocalDateTime.now(),
                    HttpStatus.OK.value(),
                    "Success",
                    String.format(JOB_RESTARTED, buildId, jobName)
            );
            return ResponseEntity.status(response.status())
                    .headers(httpHeaders)
                    .body(apiResponse);
        } else {
            throw FeignException.errorStatus("Error while restarting build: ", response);
        }
    }


    /**
     * Getting list of steps for the Stage based on {@code jobName} and {@code buildId}
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @param stageId       Stage id
     * @return List of steps
     */
    public List<StageStep> getStageStepsList(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId, int stageId) {
        return jenkinsClient.getStageStepsList(baseUri, requestHeader, jobName, buildId, stageId);
    }

    /**
     * Getting logs stage wise, based on {@code jobName} and {@code buildId}
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @param stageId       Stage id
     * @return List of step wise logs
     */
    public List<StageStepLog> getStageStepsLog(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId, int stageId) {
        Optional<ApiCache> optional = jenkinsDataService.getJenkinsData(baseUri, jobName, buildId, path.getStagesStepsLog());
        List<StageStepLog> stageStepLogs =new ArrayList<>();
        if (optional.isPresent()) {
            log.info(LOG_TEMPLATE_DATA_IN_DB);
            Map<Integer, Object> data = (Map<Integer, Object>) optional.get().getData();
            stageStepLogs = (List<StageStepLog>) data.get(String.valueOf(stageId));
        } else {
            log.info(LOG_TEMPLATE_DATA_NOT_IN_DB);
            Stage blueStageDetails = jenkinsClient.getBlueStageDetails(baseUri, requestHeader, jobName, buildId, stageId);
            if (blueStageDetails != null && blueStageDetails.isValidStatus()) {
                jenkinsDataService.insertStagesStepsLog(baseUri, requestHeader, jobName, buildId, path.getStagesStepsLog());
            }
            List<StageStep> stageSteps = jenkinsClient.getStageStepsList(baseUri, requestHeader, jobName, buildId, stageId);
//            stageStepLogs = stageSteps.stream().map(stageStep -> {
//                Response response = jenkinsClient.getStageStepLog(baseUri, requestHeader, jobName, buildId, stageId, stageStep.getId());
//                String strResponse = "";
//                Decoder dc = new Decoder.Default();
//                try {
//                    strResponse = (String) dc.decode(response, String.class);
//                } catch (IOException e) {
//                    log.error(e.getLocalizedMessage(), e);
//                }
//                return StageStepLog.builder().name(stageStep.getDisplayName()).log(strResponse).build();
//            }).collect(Collectors.toList());
        }
        return stageStepLogs;
    }

    /**
     * Getting logs stage wise for all stages, based on {@code jobName} and {@code buildId}
     *
     * @param baseUri    Jenkins Base Url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param jobName       Jenkins Job name
     * @param buildId       Jenkins Build Id
     * @return All stage logs for the build in form of map (stage id as key , step wise log as value)
     */
    public Map<String, Object> getStagesLog(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        Optional<ApiCache> optional = jenkinsDataService.getJenkinsData(baseUri, jobName, buildId, path.getStagesStepsLog());
        if (optional.isPresent()) {
            log.info(LOG_TEMPLATE_DATA_IN_DB);
            return (Map<String, Object>) optional.get().getData();
        } else {
            log.info(LOG_TEMPLATE_DATA_NOT_IN_DB);
            jenkinsDataService.insertStagesStepsLog(baseUri, requestHeader, jobName, buildId, path.getStagesStepsLog());
            return jenkinsDataService.getStagesLog(baseUri, requestHeader, jobName, buildId);
        }
    }

    /**
     * Getting Global environmental variables from jenkins server
     *
     * @param baseUri    Jenkins server url
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @return Global Environmental variables
     */
    public ResponseEntity getGlobalEnvironmentVariables(URI baseUri, Map<String, Object> requestHeader) {
        /*Getting Jenkins Crumb and adding to RequestHeader*/
        Object object = addJenkinsCrumbToRequestHeader(requestHeader, baseUri);
        if (object instanceof Map) requestHeader = (Map<String, Object>) object;
        else return (ResponseEntity) object;

        String script = String.format(JenkinsScriptConstants.JENKINS_GET_ALL_ENV);
        try (Response response = jenkinsClient.buildScriptText(baseUri, requestHeader, script)) {
//            log.info(String.format(LOG_RESPONSE_TEMPLATE, response.body(), response.headers(), response.status(), response.toString()));
//            log.info("toString(): {}", response.toString());

            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
            httpHeaders.remove("Content-Type");
            log.info("Ends");
            String strResponse = "";
            try {
                strResponse = (String) new Decoder.Default().decode(response, String.class);
                if (strResponse.contains("{")) {
                    strResponse = strResponse.substring(strResponse.indexOf('{'));
                }
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
            HttpStatus status = HttpStatus.resolve(response.status());
            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders)
                    .body(strResponse);
        }
    }

    /**
     * @param requestHeader Header info for Jenkins (like tokens, password, authentications)
     * @param baseUri    Jenkins Base Url
     * @return jenkins crumb details
     */
    private Object addJenkinsCrumbToRequestHeader(Map<String, Object> requestHeader, URI baseUri) {
        JenkinsCrumb jenkinsCrumb;
        ResponseEntity responseEntity = getJenkinsCrumb(baseUri, requestHeader);
        if (HttpStatus.OK.value() == responseEntity.getStatusCode().value())
            jenkinsCrumb = (JenkinsCrumb) responseEntity.getBody();
        else
            return responseEntity;

        assert jenkinsCrumb != null;
        String crumb = jenkinsCrumb.getCrumb();
        requestHeader.put("Jenkins-Crumb", crumb);

        return requestHeader;
    }

    /**
     * @param project Project details
     * @return Pipeline script in Jenkins config format
     */
    private String generatePipelineConfig(Project project) {
        /*Convert Project into Pipeline Script*/
        String pipeline = convertProjectToPipeline(project);
        /*Putting generated script into config.xml file and returning*/
        return Util.readConfigXML().replace(PIPELINE_SCRIPT, pipeline);
    }


    /**
     * @param project       Project details which need to update
     * @param jenkinsJobUrl jenkins job url
     */
    private void updatePipelineProject(Project project, String jenkinsJobUrl) {
        project.setLinks(Collections.singletonMap("jenkinsJob", jenkinsJobUrl));
        projectService.updateProject(project);
    }

    /**
     * @param projectId Project Id, based on this will get the project from DB
     * @return Project details
     */
    private Project getProjectById(String projectId) {

        /** Get Jenkins Project using {projectId}*/
        Optional<Project> optionalProject = projectService.getProject(projectId);
        Project project;
        if (optionalProject.isPresent()) {
            project = optionalProject.get();
        } else {
            throw new ProjectNotFoundException(projectId);
        }
        return project;
    }

    private String getJenkinsRawConfigXml(URI baseUri, Map<String, Object> requestHeader, String jobName) {
        log.info(LOG_TEMPLATE_START + String.format(LOG_TEMPLATE, jobName, baseUri));
        return jenkinsUtil.readTextFromResponse(jenkinsClient.getJenkinsConfigXml(baseUri, requestHeader, jobName));
    }

    private String convertProjectToPipeline(Project project) {
        PipelineConfigBean configBean = new PipelineConfigBean(null, null, project.getPlatform());
        PipelineString pipelineString = pipelineService.generatePipeline(project, configBean);
        return pipelineString.getPipeline();
    }

    private String updatePipelineScriptInConfig(Project project, String jenkinsRawConfigXml) {
        /*Convert Project into Pipeline Script*/
        String pipeline = convertProjectToPipeline(project);
        /*Putting generated script into config.xml file and returning*/
        return Util.getUpdatedConfigXml(pipeline, jenkinsRawConfigXml);
    }

    private ResponseEntity runScript(URI baseUri, Map<String, Object> requestHeader, String script, HttpStatus status, String msg) {
        try (Response response = jenkinsClient.buildScriptText(baseUri, requestHeader, script)) {
//            log.info(String.format(LOG_RESPONSE_TEMPLATE, response.body(), response.headers(), response.status(), response.toString()));
             HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
            httpHeaders.remove("content-type");
            httpHeaders.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
            JenkinsResponse jenkinsResponse = new JenkinsResponse(LocalDateTime.now(),
                    status.value(),
                    status.toString(),
                    msg,
                    response.request().url());
            return ResponseEntity.status(status)
                    .headers(httpHeaders)
                    .body(jenkinsResponse);
        }
    }
}
