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

import com.cognizant.workbench.pipeline.model.when.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CaseTypeDeserializer extends JsonDeserializer<CaseType> {

    private static final String FIELD_VALUE = "value" ;

    @Override
    public CaseType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        log.info("deserialize : <Started> ");
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String type = jsonNode.get("caseType").asText();
        Object object;
        switch (type) {
            case "expression":
            case "changelog":
            case "buildingTag":
            case "triggeredBy":
            case "branch":
                object = JsonUtil.getJsonString(jsonNode, FIELD_VALUE);
                break;
            case "environment":
                object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, WhenEnv.class);
                break;
            case "equals":
                object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, WhenEquals.class);
                break;
            case "changeRequest":
                if (jsonNode.get(FIELD_VALUE).has("comparator"))
                    object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, WhenChangeRequest.class);
                else
                    object = jsonNode.get(FIELD_VALUE).asText();
                break;
            case "changeset":
                if (jsonNode.get(FIELD_VALUE).has("caseSensitive"))
                    object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, WhenChangeSet.class);
                else
                    object = jsonNode.get(FIELD_VALUE).asText();
                break;
            case "tag":
                if (jsonNode.get(FIELD_VALUE).has("comparator"))
                    object = JsonUtil.getJsonClassObject(jsonNode, FIELD_VALUE, WhenTag.class);
                else
                    object = JsonUtil.getJsonString(jsonNode, FIELD_VALUE);
                break;
            default:
                object = "";
                break;
        }
        String caseValue = JsonUtil.getJsonString(jsonNode, "caseValue");

        log.info("deserialize : <Ended> ");
        return new CaseType(type, object, caseValue);
    }
}
