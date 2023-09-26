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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonPropertyOrder({"id","status","duration","startTime"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Build implements Comparable<Build> {
    private String number;
    private String url;
    private long estimatedDuration;
    private long duration;
    private String id;
    private String result;
    private long timestamp;
    private String displayName;
    private String fullDisplayName;
    private String jobName;

    @JsonProperty("status")
    public String getResult() {
        if (result == null) return "in_progress";
        switch (result) {
            case "SUCCESS":
                return "passed";
            case "ABORTED":
                return "aborted";
            case "NOT_BUILT":
                return "skipped";
            case "FAILURE":
            case "UNSTABLE":
                return "failed";
            default:
                return result;
        }
    }

    @JsonProperty("startTime")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    @Override
    public int compareTo(@NotNull Build build){
        return Long.compare(this.getTimestamp(), build.getTimestamp());
    }

}
			