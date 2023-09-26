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

package com.cognizant.workbench.pipeline.model;

/**
 * Created by 784420 on 9/27/2019 6:42 PM
 */
public class BaseConstants {
    private BaseConstants() {}

    public static final String LEAP = "leap";
    public static final String AVAILABLE = "available";
    public static final String SELECTED = "selected";

    public static final String SYSTEM_SETTINGS_ARE_NOT_FOUND = "System Settings are not found based on provided id (%s).";
    public static final String LIST_SHOULD_NOT_BE_EMPTY = "List should not be empty or null, List should be >=1 ";

    public static final String CHART_COLLECTOR = "CHART_COLLECTOR";
    public static final String LEAP_COLLECTOR = "leap.collector";
    public static final String LEAP_CHART = "leap.chart";
    public static final String LEAP_APP_STATUS = "leap.appstatus";

    public static final String USER_DOES_NOT_HAVE_THE_S_ROLE = "Invalid User to generate Collector token (User doesn't have the %s role)";
}
