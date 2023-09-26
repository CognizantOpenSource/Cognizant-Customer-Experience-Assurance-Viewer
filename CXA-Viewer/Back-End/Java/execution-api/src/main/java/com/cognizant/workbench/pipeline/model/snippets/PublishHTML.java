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
 * Created by 784420 on 7/3/2019 10:28 AM
 */
@Data
public class PublishHTML implements SnippetsConverter {

    private static final String TEMPLATE = "publishHTML target: [ allowMissing: %s, alwaysLinkToLastBuild: %s, keepAll: %s, reportDir: \"%s\", reportFiles: \"%s\", reportName: \"%s\" ]";

    private boolean allowMissing;
    private boolean alwaysLinkToLastBuild;
    private boolean keepAll = true;
    private String reportDir ;
    private String reportFiles ;
    private String reportName ;


    @JsonIgnore
    @Override
    public String convert(String type, String platform, int tabCount) {
        return String.format(TEMPLATE, allowMissing, alwaysLinkToLastBuild, keepAll, reportDir, reportFiles, reportName);
    }
}
