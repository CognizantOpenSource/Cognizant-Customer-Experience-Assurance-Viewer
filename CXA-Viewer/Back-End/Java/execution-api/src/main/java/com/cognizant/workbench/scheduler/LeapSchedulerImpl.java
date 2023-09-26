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

package com.cognizant.workbench.scheduler;

import com.cognizant.workbench.pipeline.model.AppStatus;
import com.cognizant.workbench.pipeline.repo.AppStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 11/21/2019 2:07 PM
 */
@Component("AppStatusUpdateTriggerBean")
@Slf4j
public class LeapSchedulerImpl implements ILeapScheduler {

    private static final int FIVE_MIN = 5 * 60 * 1000;
    @Autowired
    private AppStatusRepository repository;

    @Override
    public List<AppStatus> getData() {
        return repository.findAll();
    }

    @Override
    public List<AppStatus> processData(List<?> data) {
        Date now = new Date(System.currentTimeMillis() - FIVE_MIN);
        return data.stream()
                .filter(appStatus -> (((AppStatus) appStatus).getModifiedDate().before(now)) && ((AppStatus) appStatus).getStatus().equalsIgnoreCase("R"))
                .map(appStatus -> {
                    AppStatus appStatusObj = (AppStatus) appStatus;
                    appStatusObj.setStatus("F");
                    return appStatusObj;
                }).collect(Collectors.toList());
    }

    @Override
    public List<AppStatus> writeData(List processedData) {
        return repository.saveAll(processedData);
    }

    @Override
    public void processJob() {
        List<AppStatus> data = getData();
        if (!CollectionUtils.isEmpty(data)) {
            List<AppStatus> processData = processData(data);
            if (!CollectionUtils.isEmpty(processData)) {
                List<AppStatus> appStatuses = writeData(processData);
                if (!CollectionUtils.isEmpty(appStatuses)) log.info("Applications/Collectors status updated to Failed(F) status, updated count is:"+appStatuses.size());
            }else {
                log.info("All applications/collectors are active state or running in proper time ..........!");
            }
        } else {
            log.info("Unable to process data, due to data may be zero size or null");
        }
    }
}
