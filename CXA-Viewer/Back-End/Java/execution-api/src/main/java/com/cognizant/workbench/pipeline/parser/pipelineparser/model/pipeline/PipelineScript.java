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

package com.cognizant.workbench.pipeline.parser.pipelineparser.model.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineScript {
    private List<String> agentInfo = new ArrayList<>();
    private List<String> optionsInfo = new ArrayList<>();
    private List<String> paramsInfo = new ArrayList<>();
    private List<String> toolsInfo = new ArrayList<>();
    private List<String> triggerInfo = new ArrayList<>();
    private List<String> environmentInfo = new ArrayList<>();
    private List<List<String>> stagesInfo = new ArrayList<>();
    private List<String> postInfo = new ArrayList<>();
}
