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

package com.cognizant.workbench.jenkins.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class TestReport {
    int failed;
    int skipped;
    int passed;
    float passPercent;
    @JsonIgnore
    float failPercent;
    @JsonIgnore
    float skipPercent;
    @JsonIgnore
    float total;

    public TestReport() {
    }

    public TestReport(int failCount, int skipCount, int passCount) {
        this.failed = failCount;
        this.skipped = skipCount;
        this.passed = passCount;
        this.total= (float) (failCount+skipCount+passCount);
        this.passPercent=(passCount/total)*100;
        this.failPercent=(failCount/total)*100;
        this.skipPercent=(skipCount/total)*100;
    }

    public void addReport(int failCount, int skipCount, int passCount){
        this.failed += failCount;
        this.skipped += skipCount;
        this.passed += passCount;

        this.total = (float) (this.failed+this.skipped+this.passed);
        this.passPercent=(this.passed/total)*100;
        this.failPercent=(this.failed/total)*100;
        this.skipPercent=(this.skipped/total)*100;
    }
}