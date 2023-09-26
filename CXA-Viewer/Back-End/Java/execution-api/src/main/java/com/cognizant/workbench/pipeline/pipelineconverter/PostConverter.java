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

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class PostConverter {

    private static final String POST = "%s" + "post{\n";

    private static String tab(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    String generatePost(Map<String, List<String>> map, int i) {
        log.info("generatePost() : Started");
        final StringBuilder postStr = new StringBuilder();
        postStr.append(String.format(POST, tab(i)));
        map.forEach((k, v) -> {
            postStr.append(String.format("%s%s{%n", tab(i + 1), k));
            postStr.append(generateSteps(v, i + 2));
            postStr.append(tab(i + 1)).append("}\n");
        });
        postStr.append(tab(i)).append("}\n");
        log.info("generatePost() : Ended");
        return postStr.toString();
    }

    private String generateSteps(List<String> steps, final int tabCount) {
        log.info("generateSteps() : Started");
        return steps.stream().map(step -> tab(tabCount) + step + "\n").collect(Collectors.joining());
    }
}
