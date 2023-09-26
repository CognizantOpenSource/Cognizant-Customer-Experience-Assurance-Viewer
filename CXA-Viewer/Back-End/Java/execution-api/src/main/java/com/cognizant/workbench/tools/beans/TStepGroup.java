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

package com.cognizant.workbench.tools.beans;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Created by 784420 on 8/16/2019 7:15 PM
 */
@Data
public class TStepGroup {
    @NotBlank(message = "Id should not be blank")
    @Size(min = 4, message = "Id should have at least 4 characters")
    private String id;
    @NotBlank(message = "Name should not be blank")
    @Size(min = 3, message = "Name should have at least 3 characters")
    private String name;
    @NotBlank(message = "Type should not be blank")
    @Size(min = 3, message = "Type should have at least 3 characters")
    private String type;
    @NotEmpty(message = "Data should not be empty")
    @Size(min = 1, message = "Data map should have at least 1 <Kay, Value> pair")
    private Map<String, Object> data;
    @NotEmpty(message = "Params should not be empty")
    @Size(min = 1, message = "Params List should have at least 1 param")
    private List<TParam> params;
}
