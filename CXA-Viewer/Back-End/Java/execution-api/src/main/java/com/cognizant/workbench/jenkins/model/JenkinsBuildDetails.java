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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JenkinsBuildDetails {
    private String id;
    private String name;
    private String status;
    private long startTimeMillis;
    private long endTimeMillis;
    private long durationMillis;
    private String pauseDurationMillis;
    private String queueDurationMillis;
    @JsonProperty("_links.self.href")
    private String links;
    private List<Stage> stages;

    @JsonProperty("startTime")
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    @JsonProperty("duration")
    public long getDurationMillis() {
        return durationMillis;
    }

    @JsonProperty("jobName")
    public String getName() {
        return name;
    }

    public String getStatus() {
        switch (status) {
            case "SUCCESS":
                return "passed";
            case "ABORTED":
                return "aborted";
            case "NOT_BUILT":
                return "skipped";
            case "FAILURE":
            case "UNSTABLE":
            case "FAILED":
                return "failed";
            default:
                return status;
        }
    }

    @JsonIgnore
    public boolean isValidStatus() {
        switch (status) {
            case "SUCCESS":
            case "ABORTED":
            case "NOT_BUILT":
            case "FAILURE":
            case "UNSTABLE":
            case "FAILED":
                return true;
            default:
                return false;
        }
    }
}
			
	