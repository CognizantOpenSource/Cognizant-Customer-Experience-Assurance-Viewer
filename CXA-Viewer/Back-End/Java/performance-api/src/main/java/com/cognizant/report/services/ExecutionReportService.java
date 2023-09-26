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

import com.cognizant.report.models.*;
import com.cognizant.report.repos.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class ExecutionReportService {

    @Autowired
    PerformanceReportRepository performanceRepo;


    @Autowired
    RegionRepositoryForPerformance repoForPerformanceRegion;


    public Performances getPerformancesMetricsData(String projectId) {
        return performanceRepo.findTopByProjectIdOrderByTimestampDesc(projectId);
    }

    public JSONArray findRegionsForPerformance() {
        JSONArray listOfRegions = new JSONArray();
        List<RegionForPerformance> regions = repoForPerformanceRegion.findAll();
        for(RegionForPerformance regionForPerformance : regions) {
            listOfRegions.add(regionForPerformance.getRegion());
        }
        return listOfRegions;
    }

    @Retryable(
            value =  java.lang.Exception.class,
            maxAttemptsExpression   = "${retry.maxAttempts}", // retrying up to 5 times
            backoff = @Backoff(delayExpression = "${retry.maxDelay}") // 2s
    )

    public String savePerformanceData(JSONObject responseObj){
        log.info("Response object for performance =======  ");
        log.info(responseObj.get("executionId") + " = " + "ExecutionId");
        String executionId=responseObj.get("executionId") != null ? responseObj.get("executionId").toString() : "";
        String performanceURL=responseObj.get("performanceURL").toString();
        Performances result = performanceRepo.findByExecutionId(executionId);
        log.info("addPerformanceMetrics : "+ result);
        String instant = Instant.now().toString();
        result.setTimestamp(instant);
        result.setUrl(performanceURL);
        if(!(Boolean)responseObj.get("error") && responseObj.get("speedIndex") != null && responseObj.get("serverResponseTime") != null &&
                responseObj.get("largestContentfulPaint") != null && responseObj.get("firstContentfulPaint") != null
                && responseObj.get("screenshot") != null && responseObj.get("region") != null && ((List)responseObj.get("screenshot")).size() > 0){
            result.setSpeedIndex(responseObj.get("speedIndex").toString());
            result.setInteractive(responseObj.get("interactive") != null? responseObj.get("interactive").toString() : "");
            result.setServerResponseTime(responseObj.get("serverResponseTime").toString());
            result.setLargestContentfulPaint(responseObj.get("largestContentfulPaint").toString());
            result.setTotalBlockingTime(responseObj.get("totalBlockingTime") != null ? responseObj.get("totalBlockingTime").toString() : "");
            result.setFirstContentfulPaint( responseObj.get("firstContentfulPaint").toString());
            result.setCumulativeLayoutShift(responseObj.get("cumulativeLayoutShift").toString());
            result.setScreenshot((List) responseObj.get("screenshot"));
            result.setRegion(responseObj.get("region").toString()); //  For region
            result.setStatus("Completed");
            result.setError(false);
            Performances perf = performanceRepo.save(result);
        }

        else {
            result.setError(true);
            result.setErrorMessage("Scan Not Supported");
            result.setExecutionId(executionId);
            result.setStatus("InComplete");
            performanceRepo.save(result);
        }
        return "Performance Data saved to DB";
    }


 }
