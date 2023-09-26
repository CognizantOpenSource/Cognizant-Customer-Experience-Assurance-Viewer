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
import com.cognizant.workbench.pipeline.model.directives.Parameter;
import com.cognizant.workbench.pipeline.model.directives.Triggers;
import com.cognizant.workbench.pipeline.model.options.Option;
import com.cognizant.workbench.pipeline.model.snippets.StepData;
import com.cognizant.workbench.pipeline.util.PipelineDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = PipelineDeserializer.class)
public class Pipeline {

    @NotNull(message = "Agent may not be null")
    private Agent agent;
    private List<Environment> environments;
    private List<Parameter> parameters;
    @NotNull(message = "Stages may not be null")
    private List<Stage> stages;
    private Post post;
    private Triggers triggers;
    private Map<String, String> tools;
    private List<Option> options;
    private List<StepData> scriptDefinitions;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();



}
