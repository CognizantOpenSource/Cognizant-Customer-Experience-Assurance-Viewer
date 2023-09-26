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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "execNode",
        "status",
        "startTime",
        "duration"
})
public class Stage {
    private int id;
    private String name;
    private String displayName;
    private String execNode;
    private String status;
    private String result;
    private long startTimeMillis;
    private long durationInMillis;
    private long durationMillis;
    private String startTime;
    private String state;
    private String type;
    private String firstParent;
    private boolean restartable;
    private List<Edge> edges;
    @JsonProperty(value = "_links")
    private Link links;

    @JsonProperty("startTime")
    public long getStartTimeMillis() {
        if (this.startTime != null) {
            String tempStartTime = this.startTime;
            tempStartTime = tempStartTime.contains("+") ? tempStartTime.substring(0, tempStartTime.lastIndexOf('+')) : tempStartTime;
            this.startTimeMillis = LocalDateTime.parse(tempStartTime).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
            return startTimeMillis;
        }
        return startTimeMillis;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name == null ? displayName : name;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("duration")
    public long getDurationInMillis() {
        return durationInMillis;
    }


    public String getStatus() {
        if (status == null || "UNKNOWN".equalsIgnoreCase(status)) {
            if (result == null || "UNKNOWN".equalsIgnoreCase(result)) {
                if (state == null) {
                    return null;
                } else status = state;
            } else status = result;
        }
        switch (status) {
            case "SUCCESS":
                return "passed";
            case "ABORTED":
                return "aborted";
            case "NOT_BUILT":
                return "skipped";
            case "FAILURE":
            case "UNSTABLE":
                return "failed";
            case "RUNNING":
            case "IN_PROGRESS":
                return "in_progress";
            default:
                return status;
        }
    }

    @JsonIgnore
    public boolean isValidStatus() {
        if (StringUtils.isEmpty(status))
            return false;
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
			
		