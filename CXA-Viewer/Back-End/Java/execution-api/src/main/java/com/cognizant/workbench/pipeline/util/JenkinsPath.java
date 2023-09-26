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

package com.cognizant.workbench.pipeline.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by 784420 on 9/11/2019 1:29 PM
 */
@Data
@Service
public class JenkinsPath {
    @Value("${app.jenkins.client.report.path.url}")
    private String reportPath;
    @Value("${app.jenkins.client.log.path.url}")
    private String logPath;
    @Value("${app.jenkins.client.testReport.path.url}")
    private String testReportPath;
    @Value("${app.jenkins.client.job.config.url}")
    private String configPath;
    @Value("${app.jenkins.client.job.step.log.url}")
    private String stagesStepsLog;
}
