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

package com.cognizant.workbench.pipeline.model;

import com.cognizant.workbench.pipeline.model.directives.Environment;
import com.cognizant.workbench.pipeline.model.directives.Input;
import com.cognizant.workbench.pipeline.model.options.StageOption;
import com.cognizant.workbench.pipeline.model.when.When;
import com.cognizant.workbench.pipeline.util.StageDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@lombok.Data
@AllArgsConstructor
@JsonDeserialize(using = StageDeserializer.class)
public class Stage {

    private String id;
    private String type;
    private String desc;
    private String toolId;
    private boolean parallel;
    private Object data;
    private Source source;
    private Node node;
    private Input input;
    private When when;
    private List<Environment> environments;
    private List<StageOption> stageOptions;
    private Agent agent;
    private Map<String, String> tools;
    @JsonIgnore
    private Map<String, Object> additionalProperties;

    @JsonIgnore
    public List<Stage> getDataAsList() {
        return (List<Stage>) data;
    }

    @JsonIgnore
    public Data getDataAsData() {
        return (Data) data;
    }

}
