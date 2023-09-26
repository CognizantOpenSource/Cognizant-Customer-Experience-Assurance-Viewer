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

package com.cognizant.authapi.external.constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 784420 on 1/27/2021 2:00 AM
 */
public class ExternalConstants {

    private ExternalConstants() {
    }

    public static final List<String> ROBOT_PERMISSIONS = Arrays.asList("execution.tool.read", "whitelist.tool.read", "execution.log.create",
                                                                "execution.robot.create", "execution.robot.token", "execution.robot.read",
                                                                "report.force-complete", "report.merge", "report.read",
                                                                "cxa.robot.token", "robot.message.receive");

    public static final List<String> TEST_PLUGIN_PERMISSIONS = Arrays.asList("report.create");
}
