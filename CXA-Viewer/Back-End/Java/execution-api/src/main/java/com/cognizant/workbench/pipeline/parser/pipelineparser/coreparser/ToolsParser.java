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

package com.cognizant.workbench.pipeline.parser.pipelineparser.coreparser;


import com.cognizant.workbench.pipeline.parser.model.Tools;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import org.json.JSONObject;

import java.util.List;

public class ToolsParser {

    public Tools getTools(List<String> toolsInfo) {

        Tools tools = new Tools();
        if (PipelineUtil.isPipelineTag(toolsInfo.get(0), PipelineConstants.TOOLS)) {
            toolsInfo.remove(0);
            if (toolsInfo.remove(toolsInfo.size() - 1).equalsIgnoreCase("}")) {
                toolsInfo.remove(toolsInfo.size() - 1);
            }
        }

        JSONObject jsonObject = new JSONObject();
        for (String str : toolsInfo) {
            if (!str.equals("}")) {
                String[] values = str.split(" ");
                jsonObject.put(values[0], values[1].replace("'", ""));
            }
        }

        String strToolsData = jsonObject.toString();
        strToolsData = strToolsData.substring(1, strToolsData.length() - 1);

        tools.setToolsData(strToolsData);

        return tools;
    }
}
