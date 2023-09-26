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

package com.cognizant.workbench.pipeline.model.snippets;

import com.cognizant.workbench.pipeline.model.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 784420 on 1/29/2020 12:02 PM
 */
@Data
public class PerformanceTestResult {
    private static final Map<String, String> configTypeGraphType = new HashMap<>();
    @NotBlank(message = "Source data files should not be empty")
    private String sourceDataFiles;
    private String filterRegex;
    private String modeEvaluation;
    private String modeOfThreshold;
    private int errorFailedThreshold;
    private int errorUnstableThreshold;
    private List<String> errorUnstableResponseTimeThreshold;
    private double relativeUnstableThresholdNegative;
    private double relativeUnstableThresholdPositive;
    private double relativeFailedThresholdNegative;
    private double relativeFailedThresholdPositive;
    private boolean compareBuildPrevious;
    private int nthBuildNumber;
    private String configType;
    private boolean ignoreFailedBuilds;
    private boolean ignoreUnstableBuilds;
    private boolean persistConstraintLog;
    private String junitOutput;
    private String graphType;
    private String percentiles;
    private boolean modePerformancePerTestCase;
    private boolean modeThroughput;
    private boolean excludeResponseTime;
    private boolean failBuildIfNoResultFile;
    private int baselineBuild;

    static {
        configTypeGraphType.clear();
        configTypeGraphType.put("Average Response Time", "ART");
        configTypeGraphType.put("Median Response Time", "MRT");
        configTypeGraphType.put("Percentile Response Time", "PRT");
    }

    @JsonIgnore
    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    @JsonIgnore
    public String convert(int i) {
        StringBuilder buffer = new StringBuilder("perfReport");

        if (baselineBuild > 0) buffer.append(String.format(" baselineBuild: %s,", baselineBuild));
        if (compareBuildPrevious) buffer.append(String.format(" compareBuildPrevious: %s,", true));
        if (!StringUtils.isEmpty(configType) && !configType.equals(Constants.AVERAGE_RESPONSE_TIME) && configTypeGraphType.containsKey(configType)) {
            buffer.append(String.format(" configType: '%s',", configTypeGraphType.get(configType)));
        }
        if (errorFailedThreshold >= 0) buffer.append(String.format(" errorFailedThreshold: %s,", errorFailedThreshold));
        if (!CollectionUtils.isEmpty(errorUnstableResponseTimeThreshold)) {
            buffer.append(String.format(" errorUnstableResponseTimeThreshold: %s,", scriptConverter(errorUnstableResponseTimeThreshold, i+1)));
        }
        if (errorUnstableThreshold >= 0)
            buffer.append(String.format(" errorUnstableThreshold: %s,", errorUnstableThreshold));
        if (excludeResponseTime) buffer.append(String.format(" excludeResponseTime: %s,", true));
        if (!failBuildIfNoResultFile) buffer.append(String.format(" failBuildIfNoResultFile: %s,", false));
        buffer.append(String.format(" filterRegex: '%s',", Objects.toString(filterRegex, "")));
        if (!StringUtils.isEmpty(graphType) && !graphType.equals(Constants.AVERAGE_RESPONSE_TIME) && configTypeGraphType.containsKey(graphType)) {
            buffer.append(String.format(" graphType: '%s',", configTypeGraphType.get(graphType)));
        }
        if (ignoreFailedBuilds) buffer.append(String.format(" ignoreFailedBuilds: %s,", true));
        if (ignoreUnstableBuilds) buffer.append(String.format(" ignoreUnstableBuilds: %s,", true));
        if (!StringUtils.isEmpty(junitOutput)) buffer.append(String.format(" junitOutput: '%s',", junitOutput));
        if (modeEvaluation != null && modeEvaluation.equals(Constants.EVALUATION_EXPERT_MODE)) {
            buffer.append(String.format(" modeEvaluation: %s,", true));
        }
        if (modeOfThreshold != null && modeOfThreshold.equals(Constants.RELATIVE_THRESHOLD)) {
            buffer.append(String.format(" modeOfThreshold: %s,", true));
        }
        if (modePerformancePerTestCase) buffer.append(String.format(" modePerformancePerTestCase: %s,", true));
        if (modeThroughput) buffer.append(String.format(" modeThroughput: %s,", true));
        if (nthBuildNumber > 0) buffer.append(String.format(" nthBuildNumber: %s,", nthBuildNumber));
        if (percentiles != null && !percentiles.equals(Constants.PERCENTILES)) {
            buffer.append(String.format(" percentiles: '%s',", percentiles));
        }
        if (persistConstraintLog) buffer.append(String.format(" persistConstraintLog: %s,", true));
        if (relativeFailedThresholdNegative != -1) {
            buffer.append(String.format(" relativeFailedThresholdNegative: %s,", relativeFailedThresholdNegative));
        }
        if (relativeFailedThresholdPositive != -1) {
            buffer.append(String.format(" relativeFailedThresholdPositive: %s,", relativeFailedThresholdPositive));
        }
        if (relativeUnstableThresholdNegative != -1) {
            buffer.append(String.format(" relativeUnstableThresholdNegative: %s,", relativeUnstableThresholdNegative));
        }
        if (relativeUnstableThresholdPositive != -1) {
            buffer.append(String.format(" relativeUnstableThresholdPositive: %s,", relativeUnstableThresholdPositive));
        }
        buffer.append(String.format(" sourceDataFiles: '%s'", Objects.toString(sourceDataFiles, "")));

        return buffer.toString();
    }

    @JsonIgnore
    private String scriptConverter(List<String> script, int i) {
        String scriptStr = "";
        if (CollectionUtils.isEmpty(script)) return scriptStr;
        if (script.size() > 1)
            scriptStr = String.format("'''%s''' ", String.join("\n" + tabs(i), script));
        else
            scriptStr = String.format("'%s' ", String.join("\n", script));
        return scriptStr;
    }
}
