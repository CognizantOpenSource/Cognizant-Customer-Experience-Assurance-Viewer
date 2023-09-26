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
 * Created by 784420 on 6/26/2019 2:37 PM
 */
public class Constants {

    private Constants() {
    }

    /*Constants*/
    public static final String PERMISSIONS = "permissions";

    /* Level name constants */
    public static final String PIPELINE = "pipeline" ;
    public static final String STAGE = "stage" ;

    /* OS level constants */
    public static final String WINDOWS = "windows" ;
    public static final String LINUX = "linux" ;

    /* This constraints are using in the steps level validation */
    public static final String SH = "sh";
    public static final String BAT = "bat";
    public static final String PRINT = "print";
    public static final String SLEEP = "sleep";
    public static final String JUNIT = "junit";
    public static final String BUILD_JOB = "build-job";
    public static final String SLACK = "slack";
    public static final String SCRIPT = "script";
    public static final String GROOVY_SCRIPT = "groovy-script";
    public static final String CATCH_ERROR = "catchError";
    public static final String CHECKOUT = "checkout";
    public static final String SONAR = "sonar";
    public static final String PUBLISH_HTML = "publishHTML";
    public static final String ZAP = "owasp-zap";
    public static final String SONAR_ZAP = "sonar-zap-report";
    public static final String SONAR_COBERTURA = "sonar-cobertura";
    public static final String JENKINS_COBERTURA = "jenkins-cobertura";
    public static final String QUALITY_GATE = "qualityGate";
    public static final String MS_TEAM = "msTeam";
    public static final String PERFORMANCE_TEST_REPORT = "performance-test-report";

    public static final String EVALUATION_EXPERT_MODE = "Expert Mode";
    public static final String EVALUATION_STANDARD_MODE = "Standard Mode";
    public static final String RELATIVE_THRESHOLD = "Relative Threshold";
    public static final String ERROR_THRESHOLD = "Error Threshold";
    public static final String AVERAGE_RESPONSE_TIME = "Average Response Time";
    public static final String PERCENTILES = "0,50,90,100";

    /* Agent Constants for Conversion and Deserialization */
    public static final String DOCKER = "docker" ;
    public static final String DOCKERFILE = "dockerfile";
    public static final String NODE = "node";
    public static final String LABEL = "label";
    public static final String ANY = "any";
    public static final String NONE = "none";
    public static final String KUBERNETES = "kubernetes";


    /* POST level constants */
    public static final String ALWAYS = "always" ;
    public static final String SUCCESS = "success" ;
    public static final String FAILURE = "failure" ;
    public static final String CHANGED = "changed" ;
    public static final String UNSTABLE = "unstable" ;
    public static final String UNSUCCESSFUL = "unsuccessful";

    /* Config Xml Generation tag values*/
    public static final String NAME_TAG = "name";
    public static final String DESCRIPTION_TAG = "description";
    public static final String DEFAULT_VALUE_TAG = "defaultValue";
    public static final String CHOICES_TAG = "choices";
    public static final String STRING_TAG = "string";

    public static final String PROPERTIES_TAG = "properties";
    public static final String PARAMETERS_DEFINITION_PROPERTY_TAG = "hudson.model.ParametersDefinitionProperty";
    public static final String PARAMETER_DEFINITIONS_TAG = "parameterDefinitions";
    public static final String STRING_PARAMETER_DEFINITION_TAG = "hudson.model.StringParameterDefinition";
    public static final String TEXT_PARAMETER_DEFINITION_TAG = "hudson.model.TextParameterDefinition";
    public static final String BOOLEAN_PARAMETER_DEFINITION_TAG = "hudson.model.BooleanParameterDefinition";
    public static final String CHOICE_PARAMETER_DEFINITION_TAG = "hudson.model.ChoiceParameterDefinition";
    public static final String TOKEN_PARAMETER_DEFINITION_TAG = "hudson.model.PasswordParameterDefinition";
    public static final String FILE_PARAMETER_DEFINITION_TAG = "hudson.model.FileParameterDefinition";

    /*************************************************************************************************************/

    public static final String CONFIG_XML = "<?xml version='1.1' encoding='UTF-8'?>\n" +
            "<flow-definition plugin=\"workflow-job@2.31\">\n" +
            "    <actions>\n" +
            "        <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin=\"pipeline-model-definition@1.3.4.1\"/>\n" +
            "    </actions>\n" +
            "    <description></description>\n" +
            "    <keepDependencies>false</keepDependencies>\n" +
            "    <properties/>\n" +
            "    <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@2.63\">\n" +
            "        <script><![CDATA[##PipelineScript##]]></script>\n" +
            "        <sandbox>true</sandbox>\n" +
            "    </definition>\n" +
            "    <triggers/>\n" +
            "    <quietPeriod>10</quietPeriod>\n" +
            "    <disabled>false</disabled>\n" +
            "</flow-definition>";

}
