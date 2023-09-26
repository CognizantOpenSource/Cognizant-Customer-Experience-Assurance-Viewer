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

package com.cognizant.workbench.charts.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;

/**
 * Created by 784420 on 9/26/2019 6:22 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ChartProperties extends LinkedHashMap {
    private String x;
    private String y;
    private boolean isTimeSeries;
    private boolean interactive;
    private boolean pie;
    private boolean showX;
    private boolean showY;
}
