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

import com.cognizant.workbench.pipeline.model.directives.Input;
import org.springframework.util.StringUtils;

import java.util.Objects;

class InputConverter {

    private static final String INPUT = "%s" + "input{%n";
    private static final String MSG = "%s" + "message \"%s\"%n";
    private static final String OK = "%s" + "ok \"%s\"%n";
    private static final String ID = "%s" + "id \"%s\"%n";
    private static final String SUBMITTER = "%s" + "submitter \"%s\"%n";
    private static final String SUBMITTER_PARAM = "%s" + "submitterParameter \"%s\"%n";

    String convert(Input input, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.nonNull(input)) {
            stringBuilder.append(String.format(INPUT, tabs(i)));

            stringBuilder.append(String.format(MSG, tabs(i + 1), Objects.toString(input.getMessage(), "")));
            if (!StringUtils.isEmpty(input.getId()))
                stringBuilder.append(String.format(ID, tabs(i + 1), input.getId()));
            if (!StringUtils.isEmpty(input.getOk()))
                stringBuilder.append(String.format(OK, tabs(i + 1), input.getOk()));
            if (!StringUtils.isEmpty(input.getSubmitter()))
                stringBuilder.append(String.format(SUBMITTER, tabs(i + 1), input.getSubmitter()));
            if (!StringUtils.isEmpty(input.getSubmitterParameter()))
                stringBuilder.append(String.format(SUBMITTER_PARAM, tabs(i + 1), input.getSubmitterParameter()));
            if (Objects.nonNull(input.getParameters()))
                stringBuilder.append(new ParameterConverter().convert(input.getParameters(), i + 1));

            stringBuilder.append(tabs(i)).append("}\n");
        }
        return stringBuilder.toString();
    }

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }
}
