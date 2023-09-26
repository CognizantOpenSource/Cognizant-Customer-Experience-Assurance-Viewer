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
import com.cognizant.workbench.pipeline.util.StepDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by 784420 on 6/13/2019 12:41 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = StepDeserializer.class)
public class StepData {
    private String type;
    private String platform;
    private List<String> platforms;
    private Object data;
    private String changeDir;

    @JsonIgnore
    private BuildJob getDataAsBuildJob() {
        return (BuildJob) data;
    }

    @JsonIgnore
    private JunitResults getDataAsJunitResults() {
        return (JunitResults) data;
    }

    @JsonIgnore
    private Sleep getDataAsSleep() {
        return (Sleep) data;
    }

    @JsonIgnore
    public KeyValue getDataAsKeyValue() {
        return (KeyValue) data;
    }

    @JsonIgnore
    private Echo getDataAsEcho() {
        return (Echo) data;
    }

    @JsonIgnore
    private BatShScript getDataAsBatShScript() {
        return (BatShScript) data;
    }

    @JsonIgnore
    private SlackSend getDataAsSlackSend() {
        return (SlackSend) data;
    }

    @JsonIgnore
    private PlainScript getDataAsScript() {
        return (PlainScript) data;
    }

    @JsonIgnore
    private CatchError getDataAsCatchError() {
        return (CatchError) data;
    }

    @JsonIgnore
    private Checkout getDataAsCheckout() {
        return (Checkout) data;
    }

    @JsonIgnore
    private SonarQube getDataAsSonarQube() {
        return (SonarQube) data;
    }

    @JsonIgnore
    private PublishHTML getDataAsPublishHTML() {
        return (PublishHTML) data;
    }

    @JsonIgnore
    private Zap getDataAsZap() {
        return (Zap) data;
    }

    @JsonIgnore
    private SonarZap getDataAsSonarZap() {
        return (SonarZap) data;
    }

    @JsonIgnore
    private SonarCobertura getDataAsSonarCobertura() {
        return (SonarCobertura) data;
    }

    @JsonIgnore
    private JenkinsCobertura getDataAsJenkinsCobertura() {
        return (JenkinsCobertura) data;
    }

    @JsonIgnore
    private QualityGate getDataAsQualityGate() {
        return (QualityGate) data;
    }

    @JsonIgnore
    public GroovyScript getDataAsGroovyScript() {
        return (GroovyScript) data;
    }

    @JsonIgnore
    private MicrosoftTeam getDataAsMicrosoftTeam() {
        return (MicrosoftTeam) data;
    }

    @JsonIgnore
    private PerformanceTestResult getDataAsPerformanceTestResult() {
        return (PerformanceTestResult) data;
    }

    @JsonIgnore
    public String convert(String globalPlatform, int i) {
        String returnStr = "";
        platform = StringUtils.isEmpty(platform) ? globalPlatform : platform;
        switch (type) {
            case Constants.SH:
            case Constants.BAT:
                returnStr = getDataAsBatShScript().convert(type, i);
                break;
            case Constants.PRINT:
                returnStr = getDataAsEcho().convert();
                break;
            case Constants.SLEEP:
                returnStr = getDataAsSleep().convert();
                break;
            case Constants.JUNIT:
                returnStr = getDataAsJunitResults().convert();
                break;
            case Constants.BUILD_JOB:
                returnStr = getDataAsBuildJob().convert();
                break;
            case Constants.SLACK:
                returnStr = getDataAsSlackSend().convert();
                break;
            case Constants.SCRIPT:
                returnStr = getDataAsScript().convert(platform, i);
                break;
            case Constants.CATCH_ERROR:
                returnStr = getDataAsCatchError().convert();
                break;
            case Constants.CHECKOUT:
                returnStr = getDataAsCheckout().convert();
                break;
            case Constants.SONAR:
                returnStr = getDataAsSonarQube().convert(platform, i);
                break;
            case Constants.PUBLISH_HTML:
                returnStr = getDataAsPublishHTML().convert(type, platform, i);
                break;
            case Constants.ZAP:
                returnStr = getDataAsZap().convert(type, platform, i);
                break;
            case Constants.SONAR_ZAP:
                returnStr = getDataAsSonarZap().convert(type, platform, i);
                break;
            case Constants.SONAR_COBERTURA:
                returnStr = getDataAsSonarCobertura().convert(type, platform, i);
                break;
            case Constants.JENKINS_COBERTURA:
                returnStr = getDataAsJenkinsCobertura().convert(type, platform, i);
                break;
            case Constants.QUALITY_GATE:
                returnStr = getDataAsQualityGate().convert(type, platform, i);
                break;
            case Constants.GROOVY_SCRIPT:
                returnStr = getDataAsGroovyScript().convert(type, platform, i);
                break;
            case Constants.MS_TEAM:
                returnStr = getDataAsMicrosoftTeam().convert();
                break;
            case Constants.PERFORMANCE_TEST_REPORT:
                returnStr = getDataAsPerformanceTestResult().convert(i);
                break;
            default:
                break;
        }
        return returnStr;
    }
}
