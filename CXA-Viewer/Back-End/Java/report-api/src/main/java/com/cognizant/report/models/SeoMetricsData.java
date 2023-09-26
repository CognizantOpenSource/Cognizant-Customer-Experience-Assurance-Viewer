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

package com.cognizant.report.models;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.json.simple.JSONArray;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "seoMetricsData")
public class SeoMetricsData {
    @Id
    private String id;
    private String executionId;
    private String projectId;
    private String url;
    private String timestamp;
    private String status;
    private Boolean error;
    private int passScore;
    private int failScore;
    private int naScore;
    private double score;
    private JSONArray audits;
    private String errorMessage;
    private String region;
}

