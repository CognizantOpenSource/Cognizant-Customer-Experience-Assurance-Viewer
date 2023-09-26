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

package com.cognizant.workbench.pipeline.parser.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReadPipeline {

    static String PIPELINE_SCRIPT_PATH =
            Util.getResourcePath() + Util.getFileSeparator() + "pipeline-script"
                    + Util.getFileSeparator() + "test-pipelines";


    public List<String> getPipelineAsList(String fileName) {

        String path;
        List<String> pipelineList = new ArrayList<>();
        log.info("Reading the pipeline " + fileName);

        path = PIPELINE_SCRIPT_PATH + Util.getFileSeparator() + fileName + ".txt";
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            pipelineList = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pipelineList.removeAll(Arrays.asList("", null));
        return pipelineList;
    }

    public List<String> getPipelineDataAsList(String pipelineData) {
        return pipelineData.lines().filter(StringUtils::hasText).collect(Collectors.toList());
    }

}
