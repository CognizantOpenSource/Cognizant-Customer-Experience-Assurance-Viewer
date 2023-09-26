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

package com.cognizant.workbench.pipeline.parser.pipelineparser.constants;

public class PipelineConstants {

    private PipelineConstants() {}

    public static final String PIPELINE = "pipeline";
    public static final String STAGE = "stage";
    public static final String STAGES = "stages";
    public static final String ENVIRONMENT = "environment";
    public static final String AGENT_ANY = "agent any";
    public static final String AGENT = "agent";
    public static final String PARAMETERS = "parameters";
    public static final String TOOLS = "tools";
    public static final String TRIGGERS = "triggers";
    public static final String STEPS = "steps";
    public static final String OPTIONS = "options";
    public static final String WHEN = "when";
    public static final String INPUT = "input";
    public static final String POST = "post";
    public static final String POST_BRACES = "post{";
    public static final String POST_BRACES_SPACE = "post {";
    public static final String SCRIPT = "script";
    public static final String GROOVY_SCRIPT = "groovy-script";
    public static final String DOCKER = "docker";
    public static final String NODE = "node";
    public static final String KUBERNETES = "kubernetes";
    public static final String ALWAYS = "always";
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String CHANGED = "changed";
    public static final String UNSTABLE = "unstable";
    public static final String PARALLEL = "parallel";
    public static final String ALLOF = "allOf";
    public static final String ANYOF = "anyOf";
    public static final String NOT = "not";

    /*Constants*/
    public static final String IMAGE = "image";
    public static final String CASE_TYPE = "caseType";
    public static final String VALUE = "value";
    public static final String CASE_VALUE = "caseValue";
    public static final String DEFAULT = "default";
    public static final String PARAM_TYPE = "paramType";

    public static final int MAX_STAGE_COUNT = 500;

}
