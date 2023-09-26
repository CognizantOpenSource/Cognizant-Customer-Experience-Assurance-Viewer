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

import com.cognizant.report.base.models.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class PerformanceResourceTiming extends BaseModel {

    private int connectEnd;
    private int connectStart;
    private int decodedBodySize;
    private int domainLookupEnd;
    private int domainLookupStart;
    private int duration;
    private int encodedBodySize;
    private String entryType;
    private Double fetchStart;
    private String initiatorType;
    private String name;
    private String nextHopProtocol;
    private int redirectEnd;
    private int redirectStart;
    private int requestStart;
    private Double responseEnd;
    private int responseStart;
    private int secureConnectionStart;
    private List<Object> [] serverTiming;
    private Double startTime;
    private int transferSize;
    private int workerStart;

}
