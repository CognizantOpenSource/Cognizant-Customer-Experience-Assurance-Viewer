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

/**
 * Created by 784420 on 7/12/2019 4:05 PM
 */
public class JenkinsScriptConstants {

    private JenkinsScriptConstants() {
    }

    public static final String JENKINS_ADD_ENV_SCRIPT = "gnp.get(0).getEnvVars().put(\"%s\",\"%s\") %n";
    public static final String JENKINS_REMOVE_ENV_SCRIPT = "gnp.get(0).getEnvVars().remove(\"%s\") %n";
    public static final String JENKINS_BASE_SCRIPT = "gnp=Jenkins.getInstance().getGlobalNodeProperties()%n" +
                                                    "epl=gnp.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)%n" +
                                                    "if(epl==null||epl.size()==0){gnp.add(new hudson.slaves.EnvironmentVariablesNodeProperty())}%n";
    public static final String JENKINS_CONFIG_SCRIPT = "println Jenkins.getInstance().getItem(\"##jobName##\").getBuildByNumber(##buildId##).getExecution().getScript()";
    public static final String JENKINS_GET_ALL_ENV = "gnp=Jenkins.getInstance().getGlobalNodeProperties()%n" +
                                                    "epl=gnp.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)%n" +
                                                    "if(epl==null||epl.size()==0){gnp.add(new hudson.slaves.EnvironmentVariablesNodeProperty())}%n" +
                                                    "EnvVars envVars= gnp.get(0).getEnvVars()%n" +
                                                    "groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(envVars))";
}
