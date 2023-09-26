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

package com.cognizant.workbench.pipeline.parser.pipelineparser.categorization;

import com.cognizant.workbench.pipeline.parser.pipelineparser.model.pipeline.PipelineScript;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CategorizeStage extends SplitStage {

    public PipelineScript getPipelineModel() {
        return pipelineModel;
    }

    public void initiateCategorization(String pipelineData) {
        log.info("---Initiating Pipeline Categorization process----STARTED");
        List<String> pipelineRawList = getPipelineList(pipelineData);
        List<List<String>> pipelineList = splitPipeline(pipelineRawList);
        drillDownPipeline(pipelineList);
        cleansePipeline();
        handleParallelStage();
        log.info("---Pipeline Categorization process----COMPLETED");
    }

}
