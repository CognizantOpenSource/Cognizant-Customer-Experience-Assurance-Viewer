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

import com.cognizant.workbench.pipeline.model.directives.Triggers;
import com.cognizant.workbench.pipeline.model.directives.UpStreamTrigger;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TriggerDeserializer extends JsonDeserializer<Triggers> {

    private static final String VALUE_FIELD = "value" ;

    @Override
    public Triggers deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        log.debug("deserialize : <Started> ");
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        String name = JsonUtil.getJsonString(jsonNode, "name");
        Object object;
        if ("upstream".equals(name))
            object = JsonUtil.getJsonClassObject(jsonNode, VALUE_FIELD, UpStreamTrigger.class);
        else
            object = JsonUtil.getJsonString(jsonNode, VALUE_FIELD);
        log.debug("deserialize : <Ended> ");
        return new Triggers(name, object);
    }
}
