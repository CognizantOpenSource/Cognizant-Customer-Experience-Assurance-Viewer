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

import com.cognizant.report.services.ExecutionReportService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;


@Slf4j
@RestController
@RequestMapping(value = "/activemonitor")
//@CrossOrigin("*")
public class ExecutionReportController {

    @Autowired
    ExecutionReportService service;


    @PostMapping(value = "/saveMetricsData")
    public ResponseEntity saveActiveMonitor(@RequestBody JSONObject metrics) throws ParseException {
        String executionId = metrics.get("executionId").toString();
        String activeMonitorUrl = metrics.get("url").toString();
        service.saveActiveMonitorMetrics((LinkedHashMap) metrics.get("metrics"));
        return  ResponseEntity.status(200).body("Saved to DB");
    }

    @PostMapping(value = "/insertMetricsData")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public ResponseEntity addMetrics(@RequestBody JSONObject metrics,@RequestHeader("Authorization") String authToken) throws ParseException {
        log.info("Insert Metrics Execution");
        String executionId = metrics.get("executionId").toString();
        String projectId = metrics.get("projectId").toString();
        String activeMonitorUrl = metrics.get("url").toString();
        ArrayList regionAndUrl =(ArrayList) metrics.get("activeMonitorUrlAndRegion");
        String env=metrics.get("env").toString();

        service.addActiveMonitorMetrics(activeMonitorUrl, projectId, executionId, regionAndUrl,env,metrics,authToken);
        return ResponseEntity.status(200).body("Execution Started");
    }


    @GetMapping(value = "/getActiveMonitorRegionAndLoadTime")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public JSONObject getActiveMonitorRegionAndLoadTime(@RequestParam String projectId) {
        log.info("Get Active Monitor Region and Load");
        return service.getActiveMonitorAverageLoadTime(projectId);
    }


}
