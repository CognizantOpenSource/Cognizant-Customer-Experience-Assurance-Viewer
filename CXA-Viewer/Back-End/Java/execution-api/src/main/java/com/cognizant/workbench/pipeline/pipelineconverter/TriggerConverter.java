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

import com.cognizant.workbench.pipeline.model.directives.Triggers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
class TriggerConverter {

    private static final String TRIGGER = "%s" + "triggers {%n";
    private static final String CRON = "%s" + "cron('%s')%n";
    private static final String POLL_SCM = "%s" + "pollSCM('%s')%n";
    private static final String UPSTREAM = "%s" + "upstream(upstreamProjects: '%s', threshold: %s)%n";

    String convert(Triggers triggers, int i) {
        log.info("convert() : Started");
        StringBuilder stringBuilder = new StringBuilder();

        switch (triggers.getName()) {
            case "cron":
                stringBuilder.append(String.format(CRON, tabs(i + 1), triggers.getValueAsString()));
                break;
            case "pollSCM":
                stringBuilder.append(String.format(POLL_SCM, tabs(i + 1), triggers.getValueAsString()));
                break;
            case "upstream":
                stringBuilder.append(String.format(UPSTREAM, tabs(i + 1),
                        triggers.getValueAsUpStream().getUpstreamProjects(),
                        triggers.getValueAsUpStream().getThreshold())
                );
                break;
            default:
                break;
        }

        if (StringUtils.isEmpty(stringBuilder.toString())){
            return "";
        }else {
            stringBuilder.insert(0, String.format(TRIGGER, tabs(i)));
            stringBuilder.append(tabs(i)).append("}\n");
        }
        log.info("convert() : Ended");
        return stringBuilder.toString();
    }

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }
}
