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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by 784420 on 11/21/2019 2:02 PM
 */
@Component
@Slf4j
public class LeapSchedulerTrigger {
    @Autowired
    @Qualifier(value = "AppStatusUpdateTriggerBean")
    private ILeapScheduler leapScheduler;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void runScheduler() {
        log.info("**************************************** LeapSchedulerTrigger <Start> ****************************************");
        leapScheduler.processJob();
        log.info("**************************************** LeapSchedulerTrigger <End> ****************************************");
    }
}
