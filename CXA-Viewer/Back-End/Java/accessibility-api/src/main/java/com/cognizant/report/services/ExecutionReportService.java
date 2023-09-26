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

package com.cognizant.report.services;

import com.cognizant.report.models.AccessibilityGuidelines;
import com.cognizant.report.models.AccessibilityMetricsData;
import com.cognizant.report.repos.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class ExecutionReportService {

    @Autowired
    AccessibilityMetricsDataRepository accessibilityRepo;

    @Autowired
    AccessibilityGuidelinesRepository accessibilityGuidelinesRepo;

    @Value("${urlforaccessibility}")
    String urlForAccessibility;

    public AccessibilityMetricsData getAccessibilityData(String id) {
        return accessibilityRepo.findTopByProjectIdOrderByTimestampDesc(id);
    }

    public String saveAccessibilityData(JSONObject responseObj) {

        String executionid = responseObj.get("executionId") != null ?responseObj.get("executionId").toString() : "";
        AccessibilityMetricsData accessibilityMetricsData = accessibilityRepo.findByExecutionId(executionid);
        String instant = Instant.now().toString();
        String accessibilityURL = responseObj.get("url") != null ?responseObj.get("url").toString() : "";
        if(accessibilityMetricsData != null){
            //0th index = pass fail not applicable ->  1 = FAIL, NULL = New guideline, NA = -1, PASS = 0
            //1,2 and 3rd index is low, medium, high severity
            //4 = 1 = A category, 2 = AA
            Map<String, Integer[]> guidelinesStatus = new HashMap<>();
            JSONObject guidelineObject = new JSONObject();
            ArrayList<String> manualGuidelinesForA = null;
            Set<String> guidelinesForACategory = null;
            Set<String> guidelinesForAACategory = null;


            ArrayList<String> manualGuidelinesForAA = null;

            int lowSeverityGuideline = 0;
            int highSeverityGuideline = 0;
            int mediumSeverityGuideline = 0;
            int aPassGuideline = 0;
            int aFailGuideline = 0;
            int aNAGuideline = 0;

            int aaPassGuideline = 0;
            int aaFailGuideline = 0;
            int aaNAGuideline = 0;



            List<AccessibilityGuidelines> listOfAccessibilityGuidelines = accessibilityGuidelinesRepo.findAll();
            JSONObject guidelines = null;
            Set<String> guidelinesKeys = null;
            for(AccessibilityGuidelines accessibilityGuidelines : listOfAccessibilityGuidelines) {
                guidelines = (JSONObject) accessibilityGuidelines.getGuidelines();
                manualGuidelinesForA = (ArrayList<String>) (accessibilityGuidelines.getManualGuidelinesForLevelA());
                guidelinesForACategory =  new HashSet<>((ArrayList<String>) (accessibilityGuidelines.getGuidelinesForACategory()));

                guidelinesForAACategory = new HashSet<>((ArrayList<String>) (accessibilityGuidelines.getGuidelinesForAACategory()));


                manualGuidelinesForAA = (ArrayList<String>) (accessibilityGuidelines.getManualGuidelinesForLevelAA());
                guidelinesKeys = guidelines.keySet();
            }
            for(String key : guidelinesKeys) {
                guidelinesStatus.put(key, new Integer[]{null, 0,0,0,0});
            }
            if (responseObj.size() == 0 || responseObj == null || (Boolean)responseObj.get("error")) {

                accessibilityMetricsData.setTimestamp(instant);

                accessibilityMetricsData.setUrl(accessibilityURL);
                accessibilityMetricsData.setStatus("InComplete");

                accessibilityMetricsData.setError(true);
                accessibilityMetricsData.setErrorMessage("Error Occured During Scan");
                accessibilityRepo.save(accessibilityMetricsData);        }
            else {
                try{
                    int totalViolation = 0;
                    int highSeverity = 0;
                    int mediumSeverity = 0;
                    int lowSeverity = 0;
                    int aPass = 0;
                    int aFail = 0;
                    int aNA = 0;
                    int aaPass = 0;
                    int aaFail = 0;
                    int aaNA = 0;
                    int visualImpairment = 0;
                    int mobility = 0;
                    int auditory = 0;
                    int cognitive = 0;
                    JSONArray audits = new JSONArray();
                    Integer[] currentGuidelineArray = null;

                    for (Object keyStr : ((LinkedHashMap)responseObj.get("audits")).keySet()) {

                        JSONObject jsonAudit = new JSONObject();
                        String currentGuideLine = null;


                        LinkedHashMap obj =  ((LinkedHashMap)responseObj.get("audits")).get(keyStr) != null ? (LinkedHashMap) ((LinkedHashMap)responseObj.get("audits")).get(keyStr): null;

                        String scoreMode = obj != null && obj.get("scoreDisplayMode") != null && !obj.get("scoreDisplayMode").toString().equalsIgnoreCase("error")? obj.get("scoreDisplayMode").toString() : "";
                        String auditId = obj != null ? obj.get("id").toString() : "";

                        for(String guideline : guidelinesKeys) {
                            Set<String> ruleIds = new HashSet<>((List) guidelines.get(guideline));
                            if(ruleIds.contains(auditId)) {
                                jsonAudit.put("guideline", guideline);
                                currentGuideLine = guideline;
                                currentGuidelineArray = guidelinesStatus.getOrDefault(currentGuideLine, null);
                                break;
                            }
                            else {
                                currentGuideLine = null;
                            }
                        }


                        String auditDescription = obj != null  ? obj.get("description").toString() : "";
                        if(currentGuideLine != null) {
                            jsonAudit.put("title", auditId);
                            jsonAudit.put("description", auditDescription);
                        }
                        log.info(" scoreMode : "+scoreMode);
                        if(scoreMode!=null){
                            if (scoreMode.equals("binary") || scoreMode.equals("notApplicable")) {

                                switch (auditId) {
                                    case "aria-allowed-attr" :  {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if (currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3] + 1,1});

                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }

                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "aria-command-name" : {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if (currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-hidden-body": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if (currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-hidden-focus": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                aFail++;
                                                totalViolation++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aaNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-input-field-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-meter-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-progressbar-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-required-attr": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-required-children": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-required-parent": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String [] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-roles": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;

                                    }
                                    case "aria-toggle-field-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-tooltip-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "aria-valid-attr-value": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "aria-valid-attr": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "button-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "bypass": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "color-contrast": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "AA");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aaFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,2});
                                            } else {
                                                aaPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,2});
                                                }

                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aaNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,2});
                                            }
                                        }

                                        break;
                                    }
                                    case "definition-list": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "dlitem": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "document-title": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "duplicate-id-active": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "duplicate-id-aria": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "form-field-multiple-labels": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                mediumSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,currentGuidelineArray[2]+1,0,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "frame-title": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "html-has-lang": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                cognitive++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "html-lang-valid": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                cognitive++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "image-alt": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "input-image-alt": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "label": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "link-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "list": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "listitem": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "meta-refresh": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "meta-viewport": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "AA");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aaFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,2});
                                            } else {
                                                aaPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,2});
                                                }

                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aaNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,2});
                                            }
                                        }
                                        break;
                                    }
                                    case "object-alt": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "td-headers-attr": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "th-has-data-cells": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    case "valid-lang": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "AA");
                                            String[] dt = {"Visual Impairment", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                cognitive++;
                                                totalViolation++;
                                                aaFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,2});
                                            } else {
                                                aaPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,2});
                                                }

                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aaNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,2});
                                            }
                                        }
                                        break;
                                    }
                                    case "video-caption": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Auditory"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }
                                    // new keys
                                    case "role-img-alt": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "aria-roledescription": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "audio-caption": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Auditory"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }


                                    case "svg-img-alt": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment","Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                auditory++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "p-as-heading": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment","Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                auditory++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "table-fake-caption": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                auditory++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "td-has-header": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                auditory++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "link-in-text-block": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "no-autoplay-audio": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                mediumSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                cognitive++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,currentGuidelineArray[2]+1,0,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "frame-focusable-content": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "scrollable-region-focusable": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "server-side-image-map": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                lowSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,currentGuidelineArray[1]+1,0,0,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }

                                        }
                                        break;
                                    }

                                    case "blink": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                cognitive++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "marquee": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                cognitive++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "area-alt": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }


                                    case "html-xml-lang-mismatch": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                mediumSeverity++;
                                                visualImpairment++;
                                                cognitive++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,currentGuidelineArray[2]+1,0,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "duplicate-id": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                lowSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,currentGuidelineArray[1]+1,0,0,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "frame-title-unique": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "input-button-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                auditory++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "nested-interactive": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment","Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                mobility++;
                                                visualImpairment++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});

                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }

                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "select-name": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "A");
                                            String[] dt = {"Visual Impairment", "Mobility"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                visualImpairment++;
                                                mobility++;
                                                totalViolation++;
                                                aFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,1});
                                            } else {
                                                aPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,1});
                                                }
                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,1});
                                            }
                                        }
                                        break;
                                    }

                                    case "autocomplete-valid": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "AA");
                                            String[] dt = {"Visual Impairment", "Mobility", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                cognitive++;
                                                visualImpairment++;
                                                mobility++;
                                                auditory++;
                                                totalViolation++;
                                                aaFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if(currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,2});
                                            } else {
                                                aaPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,2});
                                                }


                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aaNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,2});
                                            }
                                        }
                                        break;
                                    }

                                    case "avoid-inline-spacing": {
                                        if (scoreMode.equals("binary")) {
                                            jsonAudit.put("category", "AA");
                                            String[] dt = {"Visual Impairment", "Mobility", "Cognitive"};
                                            jsonAudit.put("disabilityType", dt);
                                            long score = (int) obj.get("score");
                                            if (score == 0) {
                                                highSeverity++;
                                                cognitive++;
                                                visualImpairment++;
                                                mobility++;
                                                auditory++;
                                                totalViolation++;
                                                aaFail++;
                                                jsonAudit.put("status", "FAIL");
                                                if (currentGuidelineArray != null)
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{1,0,0,currentGuidelineArray[3]+1,2});

                                            } else {
                                                aaPass++;
                                                jsonAudit.put("status", "PASS");
                                                if(currentGuidelineArray != null && (currentGuidelineArray[0] == null || (currentGuidelineArray[0] == null || currentGuidelineArray[0] != 1))) {
                                                    guidelinesStatus.put(currentGuideLine, new Integer[]{0,0,0,0,2});
                                                }

                                            }
                                        }
                                        if (scoreMode.equals("notApplicable")) {
                                            aaNA++;
                                            if(currentGuidelineArray != null && currentGuidelineArray[0] == null) {
                                                guidelinesStatus.put(currentGuideLine, new Integer[]{-1,0,0,0,2});
                                            }
                                        }
                                        break;
                                    }
                                }

                            }

                            if (scoreMode.equals("binary")) {
                                LinkedHashMap details = (LinkedHashMap) obj.get("details");

                                ArrayList items = (ArrayList) details.get("items");
                                if(currentGuideLine != null) {

                                    jsonAudit.put("occurrence", items.size());
                                }
                                long score = (int) obj.get("score");
                                if (score == 0) {
                                    JSONObject nodeObj = new JSONObject();
                                    String [] selectorArr = new String[items.size()];
                                    String [] labelArr  = new String[items.size()];
                                    String [] pathArr  = new String[items.size()];
                                    String [] explanationArr  = new String[items.size()];
                                    String [] snippetArr  = new String[items.size()];

                                    int count = 0;
                                    for (Object item : items) {
                                        LinkedHashMap itemObj = (LinkedHashMap) item;
                                        LinkedHashMap node = (LinkedHashMap) itemObj.get("node");

                                        String selector = node.get("selector").toString();
                                        String label = node.get("nodeLabel").toString();
                                        String path = node.get("path").toString();
                                        String explanation = node.get("explanation").toString();
                                        String snippet = node.get("snippet").toString();

                                        snippetArr[count] = snippet;
                                        selectorArr[count] = selector;
                                        labelArr[count] = label;
                                        pathArr[count] = path;
                                        explanationArr[count] = explanation;
                                        count++;
                                    }
                                    nodeObj.put("selector", selectorArr);
                                    nodeObj.put("label", labelArr);
                                    nodeObj.put("path", pathArr);
                                    nodeObj.put("explanation", explanationArr);
                                    nodeObj.put("element", snippetArr);
                                    jsonAudit.put("node", nodeObj);
                                }
                                if(jsonAudit.size() > 1) {

                                    audits.add(jsonAudit);
                                }
                            }
                        }}


                    Set<String> naguidelineName = new HashSet<>();
                    Set<String> naaguidelineName = new HashSet<>();
                    Set<String> notPresent = new HashSet<>();

                    for(String key : guidelinesStatus.keySet()){
                        if (guidelinesStatus.get(key)[0] == null) {
                            notPresent.add(key);
                            if(guidelinesForACategory.contains(key)) {

                                aNAGuideline += 1;
                            } else {
                                aaNAGuideline += 1;
                            }
                        }
                        else if(guidelinesStatus.get(key)[0] != null && guidelinesStatus.get(key)[0] == 0 && guidelinesStatus.get(key)[4] == 1) {
                            aPassGuideline += 1;
                        }
                        else if(guidelinesStatus.get(key)[0] != null && guidelinesStatus.get(key)[0] == 0 && guidelinesStatus.get(key)[4] == 2) {
                            aaPassGuideline += 1;
                        }
                        else if(guidelinesStatus.get(key)[0] != null && guidelinesStatus.get(key)[0] == 1 && guidelinesStatus.get(key)[4] == 1) {
                            aFailGuideline += 1;
                            lowSeverityGuideline += guidelinesStatus.get(key)[1];
                            mediumSeverityGuideline += guidelinesStatus.get(key)[2];
                            highSeverityGuideline +=  guidelinesStatus.get(key)[3];
                        }
                        else if(guidelinesStatus.get(key)[0] != null && guidelinesStatus.get(key)[0] == -1 && guidelinesStatus.get(key)[4] == 1) {
                            aNAGuideline += 1;
                            naguidelineName.add(key);
                        }

                        else if(guidelinesStatus.get(key)[0] != null && guidelinesStatus.get(key)[0] == 1 && guidelinesStatus.get(key)[4] == 2) {
                            aaFailGuideline += 1;
                            lowSeverityGuideline += guidelinesStatus.get(key)[1];
                            mediumSeverityGuideline += guidelinesStatus.get(key)[2];
                            highSeverityGuideline +=  guidelinesStatus.get(key)[3];
                        }
                        else if(guidelinesStatus.get(key)[0] != null && guidelinesStatus.get(key)[0] == -1 && guidelinesStatus.get(key)[4] == 2) {
                            aaNAGuideline += 1;
                            naaguidelineName.add(key);
                        }
                    }

                    log.info("naaguideline ==  " +  naaguidelineName);
                    log.info("naguideline == " + naguidelineName);
                    log.info("Not Present in audits" + notPresent);
                    log.info("aNa = " + aNA);
                    log.info("aaNa = " + aaNA);
                    guidelineObject.put("aFailGuideline", aFailGuideline);
                    guidelineObject.put("aPassguideline", aPassGuideline);
                    guidelineObject.put("aNAGuideline",aNAGuideline);
                    guidelineObject.put("aaFailGuideline", aaFailGuideline);
                    guidelineObject.put("aaPassguideline", aaPassGuideline);
                    guidelineObject.put("aaNAGuideline",aaNAGuideline);
                    guidelineObject.put("lowSeverityGuideline", lowSeverityGuideline);
                    guidelineObject.put("mediumSeverityGuideline", mediumSeverityGuideline);
                    guidelineObject.put("highSeverityGuideline",highSeverityGuideline);

                    JSONArray manualGuidelinesForLevelA = new JSONArray();
                    JSONArray manualGuidelinesForLevelAA = new JSONArray();
                    for(String word : manualGuidelinesForA) {
                        manualGuidelinesForLevelA.add(word);
                    }
                    for(String word : manualGuidelinesForAA) {
                        manualGuidelinesForLevelAA.add(word);
                    }
                    guidelineObject.put("manualGuidelinesForLevelA", manualGuidelinesForLevelA);
                    guidelineObject.put("manualGuidelinesForLevelAA", manualGuidelinesForLevelAA);

                    accessibilityMetricsData.setTimestamp(instant);
                    accessibilityMetricsData.setUrl(accessibilityURL);
                    accessibilityMetricsData.setStatus("Completed");
                    accessibilityMetricsData.setError(false);
                    accessibilityMetricsData.setRegion(responseObj.get("region").toString());
                    JSONObject severity = new JSONObject();
                    JSONObject disabilityType = new JSONObject();
                    JSONObject wcag = new JSONObject();
                    severity.put("high", highSeverity);
                    severity.put("medium", mediumSeverity);
                    severity.put("low", lowSeverity);
                    disabilityType.put("visualImpairment", visualImpairment);
                    disabilityType.put("mobility", mobility);
                    disabilityType.put("auditory", auditory);
                    disabilityType.put("cognitive", cognitive);
                    wcag.put("aPass", aPass);
                    wcag.put("aFail", aFail);
                    wcag.put("aNA", aNA);
                    wcag.put("aaPass", aaPass);
                    wcag.put("aaFail", aaFail);
                    wcag.put("aaNA", aaNA);
                    accessibilityMetricsData.setSeverity(severity);
                    accessibilityMetricsData.setAudits(audits);
                    accessibilityMetricsData.setDisabilityType(disabilityType);
                    accessibilityMetricsData.setTotalViolation(totalViolation);
                    accessibilityMetricsData.setWcag(wcag);
                    accessibilityMetricsData.setGuidelineObject(guidelineObject);
                    accessibilityRepo.save(accessibilityMetricsData);
                    return "Accessibility Data added to DB";
                } catch (Exception ex) {
                    log.error("retrying " + ex );
                    if(accessibilityMetricsData != null) {
                        accessibilityMetricsData.setTimestamp(instant);
                        accessibilityMetricsData.setUrl(accessibilityURL);
                        accessibilityMetricsData.setRegion(responseObj.get("region") != null ? responseObj.get("region").toString() : "");
                        accessibilityMetricsData.setError(true);
                        accessibilityMetricsData.setErrorMessage("Scan Not Supported");
                        accessibilityRepo.save(accessibilityMetricsData);}

                }
            }}
        return "Accessibility Data saved to DB";

    }



}
