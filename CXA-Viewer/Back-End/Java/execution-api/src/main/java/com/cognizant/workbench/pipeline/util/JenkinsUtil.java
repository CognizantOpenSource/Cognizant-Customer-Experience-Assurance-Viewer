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

package com.cognizant.workbench.pipeline.util;

import com.cognizant.workbench.jenkins.model.*;
import feign.Response;
import feign.codec.DecodeException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by 784420 on 7/15/2019 4:16 PM
 */
@Service
@Slf4j
public class JenkinsUtil {

    public ResponseEntity getErrorResponse(Response response) {
        log.info("getErrorResponse(Response): ");
        ResponseEntity<String> responseEntity;
        HttpHeaders httpHeaders = new HttpHeaders();
        response.headers().forEach((key, value) -> httpHeaders.put(key, new ArrayList<>(value)));
        try {
            String responseString = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8).trim();
            responseEntity = ResponseEntity.status(response.status())
                    .headers(httpHeaders)
                    .body(getErrorResponse(responseString));
        } catch (IOException e) {
            /* added request */
            throw new DecodeException(400, "Failed to process response body.", response.request(), e);
        }
        return responseEntity;
    }

    public String getErrorResponse(String responseString) {
        log.info("getErrorResponse(String): HTML response String: {}", responseString);
        Document doc = Jsoup.parse(responseString);
        StringBuilder builder = new StringBuilder();

        Element pTag = doc.select("p").first();
        if (pTag != null && pTag.hasText())
            builder.append(pTag.text());

        Element preTag = doc.select("pre").first();
        if (preTag != null && preTag.hasText())
            builder.append(preTag.text());
        if (builder.length() > 1)
            return builder.toString();
        else
            return responseString;
    }

    public String readTextFromResponse(Response response) {
        try {
            return feign.Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return "No Data Found!";
    }

    public String xmlTagFromResponse(Response response) {
        String xml = readTextFromResponse(response);
        DocumentBuilder builder = null;
        org.w3c.dom.Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            factory.setAttribute(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);//Remediation
            factory.setAttribute(XMLInputFactory.SUPPORT_DTD, false); //Remediation
            builder = factory.newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            try {
                doc = builder.parse(src);
            } catch (SAXException | IOException | NullPointerException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        } catch (ParserConfigurationException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return doc != null ? doc.getElementsByTagName("script").item(0).getTextContent() : "";
    }

    public Map<String, TestReport> parseJenkinsStageReport(JenkinsStageReport report) {

        Map<String, TestReport> mainMap = new HashMap<>();

        for (Suite suite : report.getSuites()) {
            int failCount = 0;
            int skipCount = 0;
            int passCount = 0;
            for (Case aCase : suite.getCases()) {
                switch (aCase.getStatus()) {
                    case "PASSED":
                        passCount++;
                        break;
                    case "SKIPPED":
                        skipCount++;
                        break;
                    case "FAILED":
                        failCount++;
                        break;
                    default:
                        break;
                }
            }
            List<String> names = suite.getEnclosingBlockNames();
            for (String name : names) {
                if (!mainMap.containsKey(name))
                    mainMap.put(name, new TestReport());
                TestReport testReport = mainMap.get(name);
                testReport.addReport(failCount, skipCount, passCount);
                mainMap.put(name, testReport);
            }
        }
        return mainMap;
    }

    public JenkinsAnalysisReport parseBuilds(List<Build> builds) {
        JenkinsAnalysisReport.JenkinsAnalysisReportBuilder builder = JenkinsAnalysisReport.builder();
        Map<String, Object> duration = new HashMap<>();
        if (CollectionUtils.isEmpty(builds)) return builder.build();

        List<Long> durationList = new ArrayList<>();
        List<Build> todayBuilds = new ArrayList<>();
        Map<String, Integer> statusMap = new HashMap<>();

        long[] todayTime = getTodayTime();
        long dayStarts = todayTime[0];
        long dayEnds = todayTime[1];

        builds.forEach(build -> {
            durationList.add(build.getDuration());
            if (dayStarts <= build.getTimestamp() && build.getTimestamp() <= dayEnds) {
                todayBuilds.add(build);
            }
            String status = build.getResult();
            if (statusMap.containsKey(status)) {
                statusMap.put(status, statusMap.get(status) + 1);
            } else {
                statusMap.put(status, 1);
            }
        });

        LongSummaryStatistics longSummaryStatistics = durationList.stream().mapToLong(aLong -> aLong).summaryStatistics();
        duration.put("min", longSummaryStatistics.getMin());
        duration.put("max", longSummaryStatistics.getMax());
        duration.put("avg", longSummaryStatistics.getAverage());

        builder.buildId(builds.get(0).getId())
                .todayBuilds(todayBuilds.size())
                .totalBuilds(builds.size())
                .status(statusMap)
                .duration(duration)
                .buildDuration(durationList.get(0));

        return builder.build();
    }

    private long[] getTodayTime() {
        Calendar now = Calendar.getInstance();

        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        long dayStarts = now.getTimeInMillis();

        now.add(Calendar.DAY_OF_MONTH, 1);
        long dayEnds = now.getTimeInMillis();

        long[] longArray = new long[2];
        longArray[0] = dayStarts;
        longArray[1] = dayEnds;
        return longArray;
    }
}
