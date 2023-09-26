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

package com.cognizant.workbench.jenkins.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JenkinsJobDetails {
    private String name;
    private String color;
    private String displayName;
    private LastSuccessfulBuild lastSuccessfulBuild;
    private String description;
    private String fullDisplayName;
    private String buildable;
    private FirstBuild firstBuild;
    private LastFailedBuild lastFailedBuild;
    private LastBuild lastBuild;
    private String nextBuildNumber;
    private List<Build> builds;
    private String resumeBlocked;
    private String keepDependencies;
    private String inQueue;
    private LastCompletedBuild lastCompletedBuild;
    private String fullName;
    private HealthReport[] healthReport;
    private LastStableBuild lastStableBuild;
    private LastUnsuccessfulBuild lastUnsuccessfulBuild;
    private String url;
    private String concurrentBuild;
}
		