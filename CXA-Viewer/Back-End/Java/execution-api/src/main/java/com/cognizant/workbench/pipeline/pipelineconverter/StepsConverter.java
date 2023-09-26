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

package com.cognizant.workbench.pipeline.pipelineconverter;

import com.cognizant.workbench.pipeline.model.Constants;
import com.cognizant.workbench.pipeline.model.Data;
import com.cognizant.workbench.pipeline.model.PipelineConfigBean;
import com.cognizant.workbench.pipeline.model.snippets.StepData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 6/13/2019 3:47 PM
 */
public class StepsConverter {
    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    public String convert(Data data, int i, PipelineConfigBean configBean) {
        StringBuilder builder = new StringBuilder();
        List<String> scripts = data.getScript();
        List<StepData> steps = data.getSteps();

        String os = configBean.getPlatform();

        // if its having script it convert
        if (scripts != null && !scripts.isEmpty()) {
            if (Constants.WINDOWS.equalsIgnoreCase(os))
                builder.append(scripts.stream().map(step -> tabs(i) + "bat '" + step + "'\n").collect(Collectors.joining()));
            else
                builder.append(scripts.stream().map(step -> tabs(i) + "sh '" + step + "'\n").collect(Collectors.joining()));

        }

        // if its having Steps it will convert
        if (steps != null && !steps.isEmpty()) {
            builder.append(steps.stream().map(stepData -> tabs(i) + stepData.convert(os, i) + "\n").collect(Collectors.joining()));
        }

        return builder.toString();
    }
}
