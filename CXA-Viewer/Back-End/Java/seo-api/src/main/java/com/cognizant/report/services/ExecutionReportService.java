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

import com.cognizant.report.models.SeoMetricsData;
import com.cognizant.report.repos.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class ExecutionReportService {

    @Autowired
    SeoMetricsDataRepository seoMetricsDataRepository;


    public SeoMetricsData getSeoData(String id) {
        log.info("Get Seo Data");
         return seoMetricsDataRepository.findTopByProjectIdOrderByTimestampDesc(id);
    }

    public String saveSeoData(JSONObject responseObj){
        log.info("Response for SEO"  + " = " + responseObj);
        log.info("ExecutionId  = " + responseObj.get("executionId"));
        log.info("Seo data from lighthouse responseObj = " + responseObj);
        String executionId = responseObj.get("executionId") != null ? responseObj.get("executionId").toString() : "";

        SeoMetricsData metricsAcc = seoMetricsDataRepository.findByExecutionId(executionId);
        String projectId = metricsAcc.getProjectId();
        JSONParser jsonParser = new JSONParser();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        JSONObject jsonObject = new JSONObject();

        if(metricsAcc != null) {
            SeoMetricsData seoMetricsData = new SeoMetricsData();
            seoMetricsData.setId(metricsAcc.getId());
            seoMetricsData.setProjectId(projectId);
            seoMetricsData.setExecutionId(executionId);
            String instant = Instant.now().toString();
            if (responseObj.size() > 0 && responseObj != null && !((Boolean)responseObj.get("error")) && responseObj.get("score") != null) {
                try{
                    double score=0;
                    int passScore=0;
                    int failScore=0;
                    int naScore=0;
                    score=Double.parseDouble(responseObj.get("score").toString());
                    seoMetricsData.setScore(score);

                    List audits= (ArrayList) responseObj.get("audits");
                    String jsonStr = JSONArray.toJSONString(audits);
                    JSONArray newAudits=new JSONArray();
                    for (int i=0;i<audits.size();i++) {
                        JSONObject jsonAudit = new JSONObject();
                        LinkedHashMap obj = (LinkedHashMap) audits.get(i);
                        String scoreMode = obj.get("scoreDisplayMode").toString();
                        String auditId = obj.get("id").toString();
                        int modeScore=0;
                        String auditDescription = obj.get("description").toString();
                        jsonAudit.put("title", auditId);
                        jsonAudit.put("description", auditDescription);
                        if(scoreMode.equalsIgnoreCase("binary")){
                            if(obj.get("details")!=null){
                                JSONObject details = new JSONObject((Map)obj.get("details"));
                                ArrayList items = (ArrayList) details.get("items");
                                obj.put("occurrence", items.size());
                            }
                            else{
                                obj.put("occurrence", "NA");
                            }
                            modeScore=Integer.parseInt(obj.get("score").toString());

                        }

                        if(scoreMode.equalsIgnoreCase("binary")&&modeScore==1){
                            obj.put("status","PASS");
                            passScore++;
                            newAudits.add(obj);
                        }
                        else if(scoreMode.equalsIgnoreCase("binary")&&modeScore==0){
                            obj.put("status","FAIL");
                            failScore++;
                            newAudits.add(obj);
                        }
                        else {
                            naScore++;
                            log.info("not Applicable  "+audits.get(i));
                        }

                    }
                    seoMetricsData.setAudits(newAudits);
                    seoMetricsData.setFailScore(failScore);
                    seoMetricsData.setPassScore(passScore);
                    seoMetricsData.setNaScore(naScore);
                    seoMetricsData.setTimestamp(instant);
                    seoMetricsData.setRegion(responseObj.get("region").toString());

                    seoMetricsData.setUrl(responseObj.get("url") != null ? responseObj.get("url").toString(): "");
                    seoMetricsData.setStatus("Completed");
                    seoMetricsData.setError(false);
                    seoMetricsData.setErrorMessage("");
                    seoMetricsDataRepository.save(seoMetricsData);
                    return "Seo Data added to DB";
                } catch (Exception ex) {
                    log.error("retrying " + ex );
                    seoMetricsData.setTimestamp(instant);
                    seoMetricsData.setUrl(responseObj.get("url") != null ? responseObj.get("url").toString(): "");
                    seoMetricsData.setStatus("InProgress");
                    log.info("Exception for Seo ====== " + ex.toString());
                    seoMetricsData.setError(false);
                    seoMetricsData.setErrorMessage("Scan Not Supported");
                    seoMetricsDataRepository.save(seoMetricsData);
                    throw ex;
                }
            }
            else{
                seoMetricsData.setTimestamp(instant);
                seoMetricsData.setUrl(responseObj.get("url") != null ? responseObj.get("url").toString(): "");
                seoMetricsData.setStatus("InComplete");
                seoMetricsData.setError(true);
                log.error("Scan not supported");
                seoMetricsData.setErrorMessage("Scan Not Supported");
                seoMetricsDataRepository.save(seoMetricsData);
            }
        }
        return "Error in inserting seo data";
    }

 }
