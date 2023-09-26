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

package com.cognizant.workbench.pipeline.pipelineconverter;

import com.cognizant.workbench.pipeline.model.when.*;

import java.util.List;
import java.util.Objects;

public class WhenConverter {

    private static final String WHEN = "%s" + "when{%n";
    private static final String ALL_OF = "%s" + "allOf{%n";
    private static final String ANY_OF = "%s" + "anyOf{%n";
    private static final String NOT = "%s" + "not{%n";
    private static final String BEFORE_AGENT = "%s" + "beforeAgent true%n";
    private static final String BEFORE_INPUT = "%s" + "beforeInput true%n";
    private static final String BRANCH = "%s" + "branch '%s'%n";
    private static final String CHANGELOG = "%s" + "changelog '%s'%n";
    private static final String EXPRESSION = "%s" + "expression {%n" + "%s" + "return %s%n" + "%s" + "}%n";
    private static final String TRIGGERED_BY = "triggeredBy '%s'%n";
    private static final String CHANGE_SET = "changeset '%s'%n";
    private static final String TAG = "tag '%s'%n";

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    public String convert(When when, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.nonNull(when)) {
            stringBuilder.append(String.format(WHEN, tabs(i)));
            String whenType = when.getType();

            String whenTypeStr = whenConditions(whenType, i + 1);


            int k;
            StringBuilder temp = new StringBuilder();
            /*
              1. if inside when section any/all/not is coming increasing the tab value and storing in k as temp.
              2. looping and generating each case of that
             */
            if (Objects.nonNull(when.getCases())) {
                List<CaseType> cases = when.getCases();
                for (CaseType caseType : cases) {
                    k = "default".equals(whenType) ? i : i + 1;
                    String caseValue = caseType.getCaseValue();
                    String caseTypeStr = whenConditions(caseValue, k + 1);

                    k = "default".equals(caseValue) ? k : k + 1;
                    temp.append(String.format(caseTypeStr, convertCase(caseType, k + 1)));
                }
            }
            stringBuilder.append(String.format(whenTypeStr, temp.toString()));

            if (when.isBeforeAgent())
                stringBuilder.append(String.format(BEFORE_AGENT, tabs(i + 1)));
            if (when.isBeforeInput())
                stringBuilder.append(String.format(BEFORE_INPUT, tabs(i + 1)));

            stringBuilder.append(tabs(i)).append("}\n");
        }
        return stringBuilder.toString();
    }

    private String convertCase(CaseType caseType, int k) {
        StringBuilder stringBuilder = new StringBuilder();
        String type = caseType.getCaseType();
        switch (type) {
            case "branch":
                stringBuilder.append(String.format(BRANCH, tabs(k), caseType.getValueAsString()));
                break;
            case "buildingTag":
                stringBuilder.append(tabs(k)).append("buildingTag()\n");
                break;
            case "changelog":
                stringBuilder.append(String.format(CHANGELOG, tabs(k), caseType.getValueAsString()));
                break;
            case "expression":
                stringBuilder.append(String.format(EXPRESSION, tabs(k), tabs(k + 1), caseType.getValueAsString(), tabs(k)));
                break;
            case "environment":
                stringBuilder.append(tabs(k)).append(caseType.getValueAsWhenEnv().toConvert());
                break;
            case "equals":
                stringBuilder.append(tabs(k)).append(caseType.getValueAsWhenEquals().toConvert());
                break;
            case "triggeredBy":
                stringBuilder.append(tabs(k)).append(
                        caseType.getValue() instanceof TriggeredBy ?
                                caseType.getValueAsTriggeredBy().toConvert() :
                                String.format(TRIGGERED_BY, caseType.getValueAsString())
                );
                break;
            case "changeRequest":
                stringBuilder.append(tabs(k)).append(
                        caseType.getValue() instanceof WhenChangeRequest ?
                                caseType.getValueAsWhenChangeReq().toConvert() :
                                "changeRequest()\n"
                );
                break;
            case "changeset":
                stringBuilder.append(tabs(k)).append(
                        caseType.getValue() instanceof WhenChangeSet ?
                                caseType.getValueAsWhenChangeSet().toConvert() :
                                String.format(CHANGE_SET, caseType.getValueAsString())
                );
                break;
            case "tag":
                stringBuilder.append(tabs(k)).append(
                        caseType.getValue() instanceof WhenTag ?
                                caseType.getValueAsWhenTag().toConvert() :
                                String.format(TAG, caseType.getValueAsString())
                );
                break;
            default:
                break;
        }

        return stringBuilder.toString();
    }

    private String whenConditions(String type, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        if ("all".equals(type))
            stringBuilder.append(String.format(ALL_OF, tabs(i)));
        else if ("any".equals(type))
            stringBuilder.append(String.format(ANY_OF, tabs(i)));
        else if ("not".equals(type))
            stringBuilder.append(String.format(NOT, tabs(i)));

        stringBuilder.append("%s");

        if ("all".equals(type) || "any".equals(type) || "not".equals(type))
            stringBuilder.append(tabs(i)).append("}\n");

        return stringBuilder.toString();
    }

}
