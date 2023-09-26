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

package com.cognizant.workbench.pipeline.model.snippets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Created by 784420 on 6/13/2019 4:32 PM
 */
@Data
public class QualityGate {
    private String resultFileName;
    private String passPercentageVariable;
    private String qualityThresholdVariable;

    @JsonIgnore
    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    @JsonIgnore
    public String convert(String type, String platform, int i) {
        return String.format("sh '''source %s%n", resultFileName)+
                String.format("%s"+"echo \"Pass Rate is \"$%s%n",tabs(i), passPercentageVariable) +
                String.format("%sif [ \"$%s\" -ge \"$%s\" ]%n", tabs(i), passPercentageVariable, qualityThresholdVariable) +
                tabs(i) + "then\n" +
                String.format("%s    echo \"Pass Rate - $%s is greater than or equal to expected Threshold - $%s . ci-cd pipeline will continue to execute further\"%n", tabs(i), passPercentageVariable, qualityThresholdVariable) +
                tabs(i) + "else\n" +
                String.format("%s    echo \"Pass Rate - $%s is less than expected Threshold - $%s . ci-cd pipeline will not continue to execute further\"%n", tabs(i), passPercentageVariable, qualityThresholdVariable) +
                tabs(i) + "    exit 1\n" +
                tabs(i) + "fi'''";
    }
}
