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

import com.cognizant.workbench.charts.deserialize.ChartItemDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 784420 on 9/26/2019 6:32 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = ChartItemDeserializer.class)
public class ChartItemDTO {
    private String id;
    private String type;
    private String name;
    private ChartProperties properties;
    private ChartTemplate template;
    private Object data;
}