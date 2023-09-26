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

import com.cognizant.workbench.pipeline.model.options.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class OptionsConverter {

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    public String convert(List<Option> list, int i) {
        log.info("convert() : Started");
        StringBuilder builder = new StringBuilder();

        for (Option data : list) {
            String name = data.getName();
            switch (name) {
                case "buildDiscarder":
                    builder.append(tabs(i + 1)).append(data.getValueAsBuildDiscarder().toConvert()).append("\n");
                    break;
                case "timeout":
                    builder.append(tabs(i + 1)).append(data.getValueAsTimeout().toConvert()).append("\n");
                    break;
                case "checkoutToSubdirectory":
                    builder.append(tabs(i + 1)).append("checkoutToSubdirectory '").append(data.getValueAsString()).append("'").append("\n");
                    break;
                case "disableConcurrentBuilds":
                    builder.append(tabs(i + 1)).append("disableConcurrentBuilds()\n");
                    break;
                case "disableResume":
                    builder.append(tabs(i + 1)).append("disableResume()\n");
                    break;
                case "newContainerPerStage":
                    builder.append(tabs(i + 1)).append("newContainerPerStage()\n");
                    break;
                case "overrideIndexTriggers":
                    builder.append(tabs(i + 1)).append("overrideIndexTriggers(").append(data.getValueAsString()).append(")").append("\n");
                    break;
                case "preserveStashes":
                    String temp = StringUtils.isEmpty(data.getValueAsString()) ? "1" : data.getValueAsString();
                    builder.append(tabs(i + 1)).append("preserveStashes(buildCount: ").append(temp).append(")").append("\n");
                    break;
                case "quietPeriod":
                    builder.append(tabs(i + 1)).append("quietPeriod(").append(data.getValueAsString()).append(")").append("\n");
                    break;
                case "retry":
                    builder.append(tabs(i + 1)).append("retry(").append(data.getValueAsString()).append(")").append("\n");
                    break;
                case "skipDefaultCheckout":
                    builder.append(tabs(i + 1)).append("skipDefaultCheckout()\n");
                    break;
                case "skipStagesAfterUnstable":
                    builder.append(tabs(i + 1)).append("skipStagesAfterUnstable()\n");
                    break;
                case "timestamps":
                    builder.append(tabs(i + 1)).append("timestamps()\n");
                    break;
                case "parallelsAlwaysFailFast":
                    builder.append(tabs(i + 1)).append("parallelsAlwaysFailFast()\n");
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isEmpty(builder.toString())){
            return "";
        }else {
            builder.insert(0, String.format("%s"+"options {%n", tabs(i)));
            builder.append(tabs(i)).append("}\n");
        }
        log.info("convert() : Ended");
        return builder.toString();
    }
}
