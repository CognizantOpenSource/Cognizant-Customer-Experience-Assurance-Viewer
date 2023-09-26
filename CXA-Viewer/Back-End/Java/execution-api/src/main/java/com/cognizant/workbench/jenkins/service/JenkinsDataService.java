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

import com.cognizant.workbench.clients.JenkinsClient;
import com.cognizant.workbench.jenkins.model.*;
import com.cognizant.workbench.jenkins.repo.JenkinsDataRepository;
import com.cognizant.workbench.pipeline.util.JenkinsPath;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 8/20/2019 2:51 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JenkinsDataService {

    private static final String TEMPLATE = "%s" + "jobs/%s/builds/%s" + "%s";
    private final JenkinsDataRepository dataRepository;
    private final JenkinsClient jenkinsClient;
    private final JenkinsPath path;

    public Optional<ApiCache> getJenkinsData(URI baseUri, String jobName, int buildId, String path) {
        String url = parseURL(baseUri, jobName, buildId, path);
        return dataRepository.findById(url);
    }

    public ApiCache generateJenkinsData(Object object, URI baseUri, String jobName, int buildId, String path) {
        String url = parseURL(baseUri, jobName, buildId, path);
        log.info("Storing into DB and returning the value to user");
        return generateJenkinsDataObject(url, object.getClass().getName(), object);
    }

    public JenkinsBuildDetails insertJenkinsData(URI baseUri, Map requestHeader, String jobName, int buildId) {
        log.info("Jenkins Build Data not available in local DB, Getting from Jenkins server and returning");
        log.info("Execute method thread name: " + Thread.currentThread().getName());
        JenkinsBuildDetails buildDetails = jenkinsClient.getBuildDetails(baseUri, requestHeader, jobName, buildId);
        List<Stage> blueBuildStages = jenkinsClient.getBlueBuildDetails(baseUri, requestHeader, jobName, buildId);
        blueBuildStages.forEach(stage -> {
            if (stage.getType().equalsIgnoreCase("PARALLEL")) {
                Optional<Stage> stageOptional = buildDetails.getStages().stream().filter(tempStage -> tempStage.getName().equalsIgnoreCase(stage.getDisplayName())).findFirst();
                stageOptional.ifPresent(stage1 -> stage.setDurationInMillis(stage1.getDurationMillis()));
            }
        });
        buildDetails.setStages(blueBuildStages);
        buildDetails.setName(jobName);
        ApiCache jenkinsData = generateJenkinsData(buildDetails, baseUri, jobName, buildId, path.getReportPath());
        if (buildDetails.isValidStatus()) {
            saveJenkinsData(jenkinsData);
        }

        return buildDetails;
    }

    @Async
    public void saveJenkinsTestReportData(URI baseUri, Map requestHeader, String jobName, int buildId) {
        boolean isCompleted;
        JenkinsStageReport jenkinsStageReport;

        log.info("Jenkins Test Report Data not available in local DB, Getting from Jenkins server and returning");
        log.info("Execute Test Report asynchronously. " + Thread.currentThread().getName());
        jenkinsStageReport = jenkinsClient.getJenkinsStageReport(baseUri, requestHeader, jobName, buildId);

        ApiCache jenkinsData = generateJenkinsData(jenkinsStageReport, baseUri, jobName, buildId, path.getTestReportPath());
        saveJenkinsData(jenkinsData);
        isCompleted = true;
        log.info("Stored Jenkins TestReport data per the build isCompleted: " + isCompleted);
    }

    @Async
    public void saveJenkinsLogData(URI baseUri, Map requestHeader, String jobName, int buildId) {
        boolean isCompleted;
        log.info("Jenkins logs Data not available in local DB, Getting from Jenkins server and returning");
        log.info("Execute logs data asynchronously. " + Thread.currentThread().getName());
        Response response = jenkinsClient.getBuildLogs(baseUri, requestHeader, jobName, buildId);
        Decoder dc = new Decoder.Default();
        String strResponse = "";

        try {
            strResponse = (String) dc.decode(response, String.class);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        ApiCache jenkinsData = generateJenkinsData(strResponse, baseUri, jobName, buildId, path.getLogPath());
        saveJenkinsData(jenkinsData);
        isCompleted = true;
        log.info("Stored Jenkins log data per the build isCompleted: " + isCompleted);
    }

    @Async
    public void saveJenkinsConfigData(URI baseUri, String jobName, int buildId, String strResponse, String configPath) {
        boolean isCompleted;
        log.info("Jenkins Config Data not available in local DB, Getting from Jenkins server and returning");
        log.info("Execute Config data asynchronously. " + Thread.currentThread().getName());
        ApiCache jenkinsData = generateJenkinsData(strResponse, baseUri, jobName, buildId, configPath);
        saveJenkinsData(jenkinsData);
        isCompleted = true;
        log.info("Stored Jenkins Config data per the build isCompleted: " + isCompleted);
    }

    @Async
    public void insertStagesStepsLog(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId, String pathStagesStepsLog) {
        Map<String, Object> map = getStagesLog(baseUri, requestHeader, jobName, buildId);
        ApiCache jenkinsData = generateJenkinsData(map, baseUri, jobName, buildId, pathStagesStepsLog);
        if (map != null) saveJenkinsData(jenkinsData);
    }

    public Map<String, Object> getStagesLog(URI baseUri, Map<String, Object> requestHeader, String jobName, int buildId) {
        List<Stage> stages = jenkinsClient.getBlueBuildDetails(baseUri, requestHeader, jobName, buildId);
        Map<String, Object> map = new HashMap<>();
//        stages.forEach(stage -> {
//            List<StageStepLog> stageStepLogs = jenkinsClient.getStageStepsList(baseUri, requestHeader, jobName, buildId, stage.getId())
//                    .stream().map(stageStep -> {
//                        Response response = jenkinsClient.getStageStepLog(baseUri, requestHeader, jobName, buildId, stage.getId(), stageStep.getId());
//                        String strResponse = "";
//                        Decoder dc = new Decoder.Default();
//                        try {
//                            strResponse = (String) dc.decode(response, String.class);
//                        } catch (IOException e) {
//                            log.error(e.getLocalizedMessage(), e);
//                        }
//                        return StageStepLog.builder().name(stageStep.getDisplayName()).log(strResponse).build();
//                    }).collect(Collectors.toList());
//            map.put(String.valueOf(stage.getId()), stageStepLogs);
//        });
        return map;
    }

    public ApiCache saveJenkinsData(ApiCache jenkinsData) {
        return dataRepository.save(jenkinsData);
    }

    private String parseURL(URI baseUri, String jobName, int buildId, String path) {
        return String.format(TEMPLATE, baseUri, jobName, buildId, path);
    }

    private ApiCache generateJenkinsDataObject(String url, String className, Object jsonData) {
        ApiCache jenkinsData = new ApiCache();
        jenkinsData.setUrl(url);
        jenkinsData.setClassType(className);
        jenkinsData.setData(jsonData);
        return jenkinsData;
    }
}
