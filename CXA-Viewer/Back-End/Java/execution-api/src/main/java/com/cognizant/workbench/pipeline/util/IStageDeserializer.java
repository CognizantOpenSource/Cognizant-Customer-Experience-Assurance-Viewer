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

import com.cognizant.workbench.pipeline.model.IStage;
import com.cognizant.workbench.pipeline.model.Stage;
import com.cognizant.workbench.pipeline.model.StageGroup;
import com.cognizant.workbench.pipeline.model.StageUnion;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class IStageDeserializer extends StdDeserializer<StageUnion> {

    private static final long serialVersionUID = 1L;
    private ObjectMapper objectMapper = new ObjectMapper();
    JavaType stageType = objectMapper.getTypeFactory().constructCollectionType(List.class, Stage.class);

    private IStageDeserializer(Class<StageUnion> classType) {
        super(classType);
    }

    public IStageDeserializer() {
        this(null);
    }

    @Override
    public StageUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        log.info("deserialize : <Started> ");
        IStage iStage;

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        boolean isParallel = node.has("parallel") && node.get("parallel").asBoolean();

        if (isParallel) {
            iStage = new StageGroup();
        } else {
            return (StageUnion) (IStage) objectMapper.convertValue(node, Stage.class);
        }

        log.info("deserialize : <Started> ");
        return (StageUnion) iStage;
    }
}
