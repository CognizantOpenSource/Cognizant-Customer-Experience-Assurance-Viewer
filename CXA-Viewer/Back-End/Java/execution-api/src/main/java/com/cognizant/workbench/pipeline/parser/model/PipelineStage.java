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

package com.cognizant.workbench.pipeline.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineStage {

    private String id;
    private String type;
    private String description;
    private String toolId;
    private boolean parallel;

    private String stageData;

    private boolean isStep=false;
    private String step_ftl;

    private boolean isAgent=false;
    private String agent_ftl;

    private boolean isSource=false;
    private  String source_ftl;

    private List<PipelineStage> stages = new ArrayList<>();
    private boolean isChildStage;
    private String childStageDate;

    private Boolean isEnvironment=false;
    private String environment_ftl;

    private Boolean isInput=false;
    private String input_ftl;

    private Boolean isOptions=false;
    private String options_ftl;

    private Boolean isWhen=false;
    private String when_ftl;
}
