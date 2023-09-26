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

package com.cognizant.workbench.charts.deserialize;

import com.cognizant.workbench.charts.beans.*;
import com.cognizant.workbench.pipeline.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by 784420 on 10/23/2019 1:18 PM
 */
@Slf4j
public class ChartItemDeserializer extends JsonDeserializer<ChartItemDTO> {

    @Override
    public ChartItemDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        log.info("deserialize : <Started> ");
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String id = JsonUtil.getJsonString(jsonNode, "id");
        String type = JsonUtil.getJsonString(jsonNode, "type");
        String name = JsonUtil.getJsonString(jsonNode, "name");
        ChartProperties properties = (ChartProperties) JsonUtil.getJsonClassObject(jsonNode, "properties", ChartProperties.class);
        ChartTemplate template = (ChartTemplate) JsonUtil.getJsonClassObject(jsonNode, "template", ChartTemplate.class);
        Object data;
        if (type.equalsIgnoreCase("data-grid")){
            data = JsonUtil.getJsonClassObject(jsonNode, "data", DataGridChartData.class);
        } else {
            data = JsonUtil.getJsonClassList(jsonNode, "data", ChartData.class);
        }

        return new ChartItemDTO(id, type, name, properties, template, data);
    }
}
