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

import com.cognizant.workbench.pipeline.model.Agent;
import com.cognizant.workbench.pipeline.model.Pipeline;
import com.cognizant.workbench.pipeline.model.Post;
import com.cognizant.workbench.pipeline.model.Stage;
import com.cognizant.workbench.pipeline.model.directives.Environment;
import com.cognizant.workbench.pipeline.model.directives.Parameter;
import com.cognizant.workbench.pipeline.model.directives.Triggers;
import com.cognizant.workbench.pipeline.model.options.Option;
import com.cognizant.workbench.pipeline.model.snippets.StepData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class PipelineDeserializer extends JsonDeserializer<Pipeline> {
    @Override
    public Pipeline deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        List<Parameter> parameters = null;
        List<Environment> environments = null;
        Post post = null;  // Creating Object and setting the values
        Triggers triggers = null;
        Map<String, String> tools = null;
        List<Option> options = null;
        final List<Stage> stages = (List<Stage>) JsonUtil.getJsonClassList(jsonNode, "stages", Stage.class);
        final Map<String, Object> additionalProperties = (Map<String, Object>) JsonUtil.getJsonClassMap(jsonNode, "additionalProperties", String.class, Object.class);
        /* Environments Section*/
        if (jsonNode.has("environments")) {
            environments = (List<Environment>) JsonUtil.getJsonClassList(jsonNode, "environments", Environment.class);
        }
        /*Parameters Section*/
        if (jsonNode.has("parameters")) {
            parameters = (List<Parameter>) JsonUtil.getJsonClassList(jsonNode, "parameters", Parameter.class);
        }
        /*Post Section*/
        if (jsonNode.has("post")) {
            post = (Post) JsonUtil.getJsonClassObject(jsonNode, "post", Post.class);
        }
        /*Trigger Section*/
        if (jsonNode.has("triggers")) {
            triggers = (Triggers) JsonUtil.getJsonClassObject(jsonNode, "triggers", Triggers.class);
        }
        /*Tools Section*/
        if (jsonNode.has("tools")) {
            tools = JsonUtil.getJsonStringMap(jsonNode, "tools");
        }
        /*Options Section*/
        if (jsonNode.has("options")) {
            options = (List<Option>) JsonUtil.getJsonClassList(jsonNode, "options", Option.class);
        }
        /*Agent Section*/
        Agent agent = (Agent) JsonUtil.getJsonClassObject(jsonNode, "agent", Agent.class);
        List<StepData> scriptDefinitions = (List<StepData>) JsonUtil.getJsonClassList(jsonNode, "scriptDefinitions", StepData.class);
        return new Pipeline(agent, environments, parameters, stages, post, triggers, tools, options, scriptDefinitions, additionalProperties);
    }
}
