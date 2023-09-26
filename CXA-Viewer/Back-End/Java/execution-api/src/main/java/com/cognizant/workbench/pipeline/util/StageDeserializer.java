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
import com.cognizant.workbench.pipeline.model.directives.Environment;
import com.cognizant.workbench.pipeline.model.directives.Input;
import com.cognizant.workbench.pipeline.model.options.StageOption;
import com.cognizant.workbench.pipeline.model.when.When;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class StageDeserializer extends JsonDeserializer<Stage> {

    @Override
    public Stage deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        Map<String, String> tools = null;

        final String id = jsonNode.get("id").asText();
        final String type = jsonNode.get("type").asText();
        final String desc = jsonNode.has("desc") ? jsonNode.get("desc").asText() : "";
        final String toolId = jsonNode.has("toolId") ? jsonNode.get("toolId").asText() : "";
        final boolean parallel = jsonNode.has("parallel") && jsonNode.get("parallel").asBoolean();

        final Node node = (Node) JsonUtil.getJsonClassObject(jsonNode, "node", Node.class);
        final Source source = (Source) JsonUtil.getJsonClassObject(jsonNode, "source", Source.class);
        final Map<String, Object> additionalProperties = (Map<String, Object>) JsonUtil.getJsonClassMap(jsonNode, "additionalProperties", String.class, Object.class);
        final Input input = (Input) JsonUtil.getJsonClassObject(jsonNode, "input", Input.class);

        final When when = (When) JsonUtil.getJsonClassObject(jsonNode, "when", When.class);

        final List<StageOption> stageOptions = (List<StageOption>) JsonUtil.getJsonClassList(jsonNode, "stageOptions", StageOption.class);

        final Agent agent = (Agent) JsonUtil.getJsonClassObject(jsonNode, "agent", Agent.class);

        List<Environment> environments = null;
        if (jsonNode.has("environments")) {
            environments = (List<Environment>) JsonUtil.getJsonClassList(jsonNode, "environments", Environment.class);
        }
        /*Tools Section*/
        if (jsonNode.has("tools")) {
            tools = JsonUtil.getJsonStringMap(jsonNode, "tools");
        }
        Object data;
        if (parallel) {
            data = JsonUtil.getJsonClassList(jsonNode, "data", Stage.class);
        } else {
            data = JsonUtil.getJsonClassObject(jsonNode, "data", Data.class);
        }
        return new Stage(id, type, desc, toolId, parallel, data, source, node, input, when, environments, stageOptions, agent, tools, additionalProperties);
    }
}