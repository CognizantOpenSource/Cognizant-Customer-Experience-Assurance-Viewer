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

package com.cognizant.report.controllers;

import com.cognizant.report.models.*;
import com.cognizant.report.services.ExecutionReportService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.regex.Matcher;

@Slf4j
@RestController
@RequestMapping(value = "/performance")
//@CrossOrigin("*")
public class ExecutionReportController {

    @Autowired
    ExecutionReportService service;

    @GetMapping("/getAllRegionsForPerformance")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public JSONArray getRegionsForPerformance(){
        return service.findRegionsForPerformance();
    }

    @GetMapping(value = "/getPerformances")
    @PreAuthorize("hasPermission('TestReport','cxa.report.read')")
    public Performances getPerformancesMetrics(@RequestParam String projectId) {
        return service.getPerformancesMetricsData(projectId);
    }

    @PostMapping(value = "/insertMetricsData")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public ResponseEntity addMetrics(@RequestBody JSONObject metrics) throws ParseException {

        String executionId=metrics.get("executionId").toString();
        String performanceURL=metrics.get("performanceURL").toString();
        Boolean flag = true;
        Set<String> keys = metrics.keySet();
        for(String key : keys) {
            log.info("KEy and values are " + key + " === " + metrics.get(key).toString());
            if(metrics.get(key) != null && !key.equals("error") && !key.equalsIgnoreCase("screenshot")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9:.,/?=&_\\-\\s]+$");
                if(key.equalsIgnoreCase("performanceURL")) {
                    pattern =java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                }
                String temp = "";
                String currentValue = metrics.get(key).toString();
                for(int i = 0; i < currentValue.length(); i++) {
                    int j = Integer.valueOf(currentValue.charAt(i));
                    if(j == 160) {
                        temp += " ";
                    }
                    else {
                        temp += (char)j;
                    }
                }
                Matcher matcher = pattern.matcher(temp);

                if(matcher.matches()) {
//                    log.info("Valid field in Performance= " + key);
                }
                else{
                    flag = false;
                    break;
                }

            }
        }
        if(flag) {
            String res = service.savePerformanceData(metrics);
            log.info(" inside Performance");
            return ResponseEntity.status(200).body("Started Execution");
        }

        return ResponseEntity.status(200).body("Not Completed");
    }


}
