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

import lombok.Data;

import java.util.List;

@Data
public class Case {
    private String errorStackTrace;
    private String stdout;
    private String className;
    private String stderr;
    private boolean skipped;
    private double duration;
    private List<String> testActions;
    private String name;
    private int failedSince;
    private String skippedMessage;
    private int age;
    private String errorDetails;
    private String status;
}
	