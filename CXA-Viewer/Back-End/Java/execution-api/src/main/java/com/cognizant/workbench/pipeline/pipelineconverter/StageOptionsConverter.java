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

import com.cognizant.workbench.pipeline.model.options.StageOption;

import java.util.List;

public class StageOptionsConverter {

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    public String convert(List<StageOption> list, int i) {
        StringBuilder builder = new StringBuilder();

        builder.append(tabs(i)).append("options {\n");
        for (StageOption data : list) {
            String option = data.getName();
            builder.append(tabs(i + 1));
            switch (option) {
                case "timeout":
                    builder.append(data.getValueAsTimeout().toConvert());
                    break;
                case "retry":
                    builder.append("retry(").append(data.getValueAsString()).append(")");
                    break;
                case "skipDefaultCheckout":
                    builder.append("skipDefaultCheckout()");
                    break;
                case "timestamps":
                    builder.append("timestamps()");
                    break;
                default:
                    break;
            }
            builder.append('\n');
        }
        builder.append(tabs(i)).append("}\n");

        return builder.toString();
    }
}
