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

import com.cognizant.workbench.pipeline.model.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by 784420 on 7/1/2019 6:35 PM
 */
@Slf4j
public class AgentDeserializer extends JsonDeserializer<Agent> {

    private static final String DATA_FIELD = "data";

    @Override
    public Agent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        log.info("deserialize : <Started> ");

        JsonNode jsonNode = jp.getCodec().readTree(jp);

        final String id = JsonUtil.getJsonString(jsonNode, "id");
        final String type = JsonUtil.getJsonString(jsonNode, "type");
        final String name = JsonUtil.getJsonString(jsonNode, "name");
        final String agentType = JsonUtil.getJsonString(jsonNode, "agentType");

        final Object data;
        switch (agentType) {
            case Constants.DOCKER:
                data = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, Docker.class);
                break;
            case Constants.DOCKERFILE:
                data = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, DockerFile.class);
                break;
            case Constants.NODE:
                data = JsonUtil.getJsonStringList(jsonNode.get(DATA_FIELD), "label");
                break;
            case Constants.LABEL:
                data = JsonUtil.getJsonString(jsonNode.get(DATA_FIELD), "name");
                break;
            case Constants.ANY:
                data = Constants.ANY;
                break;
            case Constants.NONE:
                data = Constants.NONE;
                break;
            case Constants.KUBERNETES:
                data = JsonUtil.getJsonClassObject(jsonNode, DATA_FIELD, KubernetesAgent.class);
                break;
            default:
                data = "any";
                break;
        }

        log.info("deserialize : <Ended> ");
        return new Agent(id, name, type, agentType, data);
    }
}
