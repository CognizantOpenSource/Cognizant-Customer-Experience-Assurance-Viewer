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

package com.cognizant.report.models;

import com.cognizant.report.base.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

import static com.cognizant.report.common.Constants.APP_FAILURE_ANALYZER_ENABLE;
import static com.cognizant.report.models.ReportApiSettings.SettingsType.CLIENT;

@Document(collection = "reportApiSettings")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ReportApiSettings extends BaseModel {
    @Id
    private SettingsType type = CLIENT;
    private Map<String, Object> settings = new HashMap<>();

    public enum SettingsType{
        CLIENT
    }

    public void setFailureAnalyzerEnable(boolean isEnabled){
        this.settings.put(APP_FAILURE_ANALYZER_ENABLE, isEnabled);
    }

    public boolean getFailureAnalyzerEnable(){
        return (boolean) this.settings.getOrDefault(APP_FAILURE_ANALYZER_ENABLE, false);
    }
}
