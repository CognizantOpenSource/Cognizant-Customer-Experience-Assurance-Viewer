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

import com.cognizant.workbench.pipeline.model.options.StageOption;
import com.cognizant.workbench.pipeline.model.options.Timeout;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StageOptionsDeserializer extends JsonDeserializer<StageOption> {

    private static final String VALUE_FIELD = "value" ;

    @Override
    public StageOption deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        log.info("deserialize : <Started> ");
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String option = JsonUtil.getJsonString(jsonNode, "name");
        Object object = null;
        switch (option) {
            case "timeout":
                object = JsonUtil.getJsonClassObject(jsonNode, VALUE_FIELD, Timeout.class);
                break;
            case "retry":
            case "skipDefaultCheckout":
            case "timestamps":
                object = JsonUtil.getJsonString(jsonNode, VALUE_FIELD);
                break;
            default:
                break;
        }

        log.info("deserialize : <Ended> ");
        return new StageOption(option, object);
    }
}
