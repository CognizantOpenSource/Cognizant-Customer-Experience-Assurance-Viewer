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

import com.cognizant.workbench.pipeline.model.options.BuildDiscarder;
import com.cognizant.workbench.pipeline.model.options.Option;
import com.cognizant.workbench.pipeline.model.options.Timeout;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class OptionsDeserializer extends JsonDeserializer<Option> {

    private static final String FIELD_VALUE = "value" ;

    @Override
    public Option deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        log.info("deserialize : <Started> ");
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String name = JsonUtil.getJsonString(jsonNode, "name");
        Object object = null;
        switch (name) {
            case "buildDiscarder":
                object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, BuildDiscarder.class);
                break;
            case "timeout":
                object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, Timeout.class);
                break;
            case "checkoutToSubdirectory":
            case "disableConcurrentBuilds":
            case "disableResume":
            case "newContainerPerStage":
            case "overrideIndexTriggers":
            case "preserveStashes":
            case "quietPeriod":
            case "retry":
            case "skipDefaultCheckout":
            case "skipStagesAfterUnstable":
            case "timestamps":
            case "parallelsAlwaysFailFast":
                object = JsonUtil.getJsonString(jsonNode, FIELD_VALUE);
                break;
            default:
                break;
        }

        log.info("deserialize : <Ended> ");
        return new Option(name, object);
    }
}
