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

import com.cognizant.workbench.pipeline.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * Created by 784420 on 1/28/2020 4:55 PM
 */
@Data
public class MicrosoftTeam {
    @NotBlank(message = "WebHookUrl should not be empty")
    private String webHookUrl;
    private String status;
    private String message;
    private String type;

    @JsonIgnore
    public String convert(){
        StringBuilder buffer = new StringBuilder("office365ConnectorSend");

        if (!StringUtils.isEmpty(type)) buffer.append(String.format(" color: \"%s\",", Util.getColour(type)));
        if (!StringUtils.isEmpty(message)) buffer.append(String.format(" message: \"%s\",", message));
        if (!StringUtils.isEmpty(status)) buffer.append(String.format(" status: \"%s\",", status));
        buffer.append(String.format(" webhookUrl: \"%s\"", webHookUrl));

        return buffer.toString();
    }
}
