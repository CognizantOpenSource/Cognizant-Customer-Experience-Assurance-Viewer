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

import com.cognizant.workbench.pipeline.model.Constants;
import com.cognizant.workbench.pipeline.model.snippets.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Created by 784420 on 6/13/2019 12:46 PM
 */
@Slf4j
public class StepDeserializer extends JsonDeserializer<StepData> {

    private static final String DATA_FIELD = "data" ;

    @Override
    public StepData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        log.info("deserialize() : <Started> ");

        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        String type = jsonNode.get("type").asText();
        String platform = JsonUtil.getJsonString(jsonNode, "platform");
        String changeDir = JsonUtil.getJsonString(jsonNode, "changeDir");
        List<String> platforms = JsonUtil.getJsonStringList(jsonNode, "platforms");

        Object object = null;
        switch (type) {
            case Constants.SH:
            case Constants.BAT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, BatShScript.class);
                break;
            case Constants.PRINT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, Echo.class);
                break;
            case Constants.SLEEP:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, Sleep.class);
                break;
            case Constants.JUNIT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, JunitResults.class);
                break;
            case Constants.BUILD_JOB:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, BuildJob.class);
                break;
            case Constants.SLACK:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, SlackSend.class);
                break;
            case Constants.SCRIPT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, PlainScript.class);
                break;
            case Constants.CATCH_ERROR:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, CatchError.class);
                break;
            case Constants.CHECKOUT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, Checkout.class);
                break;
            case Constants.SONAR:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, SonarQube.class);
                break;
            case Constants.PUBLISH_HTML:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, PublishHTML.class);
                break;
            case Constants.ZAP:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, Zap.class);
                break;
            case Constants.SONAR_ZAP:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, SonarZap.class);
                break;
            case Constants.SONAR_COBERTURA:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, SonarCobertura.class);
                break;
            case Constants.JENKINS_COBERTURA:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, JenkinsCobertura.class);
                break;
            case Constants.QUALITY_GATE:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, QualityGate.class);
                break;
            case Constants.GROOVY_SCRIPT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, GroovyScript.class);
                break;
            case Constants.MS_TEAM:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, MicrosoftTeam.class);
                break;
            case Constants.PERFORMANCE_TEST_REPORT:
                object = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, PerformanceTestResult.class);
                break;
            default:
                break;
        }

        log.info("deserialize() : <Ended> ");
        return new StepData(type, platform, platforms, object, changeDir);
    }
}
