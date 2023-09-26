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

package com.cognizant.workbench.init.constants;

/**
 * Created by 784420 on 8/16/2019 5:16 PM
 */
public class InitDBCons {

    public static final String SYSTEM_NAME = "cxa-workbench-api";

    public static final String PERMISSION_JSON_FILE = "/init/json/scripts/permission.json";
    public static final String SOURCE_STAGE_JSON_FILE = "/init/json/scripts/toolSourceStage.json";

    public static final String TEST_STAGE_JSON_FILE = "/init/json/scripts/toolTestStage.json";
    public static final String DEPLOY_STAGE_JSON_FILE = "/init/json/scripts/toolDeployStage.json";
    public static final String AGENT_STAGE_JSON_FILE = "/init/json/scripts/toolAgentStage.json";
    public static final String STAGE_STEP_JSON_FILE = "/init/json/scripts/toolStageStep.json";
    public static final String AUTO_SUGGEST_JSON_FILE = "/init/json/scripts/autoSuggest.json";
    public static final String SYSTEM_SETTING_JSON = "/init/json/scripts/systemSetting.json";
    public static final String PROJECT_CONFIG_JSON = "/init/json/scripts/toolProjectConfig.json";
    public static final String IMAGES_JSON_FILE = "/init/json/scripts/images.json";
    public static final String WHITELIST_URL_JSON_FILE = "/init/json/scripts/whitelistUrl.json";

    private InitDBCons() {
    }
}
