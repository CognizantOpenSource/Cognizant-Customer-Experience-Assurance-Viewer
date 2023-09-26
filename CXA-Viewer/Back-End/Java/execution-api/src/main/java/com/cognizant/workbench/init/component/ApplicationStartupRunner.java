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

package com.cognizant.workbench.init.component;

import com.cognizant.workbench.base.models.WhiteList;
import com.cognizant.workbench.base.repos.WhiteListRepository;
import com.cognizant.workbench.pipeline.service.ProjectService;
import com.cognizant.workbench.seleniumgrid.beans.Image;
import com.cognizant.workbench.seleniumgrid.services.DockerComposeService;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationStartupRunner {
    private InitialDBConfig dbConfig;

    private ProjectService projectService;
    private ToolsData toolsData;
    private DockerComposeService dockerComposeService;
    private WhiteListRepository whiteListRepository;

    public void run() throws IOException {
        assertUser();
        assertRoles();
        assertProjects();
        assertTools();
        assertSeleniumGridImages();
        assertWhitelistUrls();
    }

    private void assertWhitelistUrls() throws IOException {
        String name = "WhitelistUrl's";
        log.info("Getting all {} from DB.", name);
        List<WhiteList> all = whiteListRepository.findAll();
        if (Collections.isEmpty(all)){
            log.info("No {} are available in DB......! ", name);
            List<WhiteList> list = dbConfig.insertWhitelistUrls();
            log.info("{} are inserted successfully. Permission count post insertion is:{} ", name, list.size());
        }else {
            log.info("{} are available in DB with count:{} ", name, all.size());
        }
    }

    private void assertSeleniumGridImages() throws IOException {
        String name = "SeleniumGridImages";
        log.info("Getting all {} from DB.", name);
        List<Image> all = dockerComposeService.getAll();
        if (Collections.isEmpty(all)){
            log.info("No {} are available in DB......! ", name);
            List<Image> list = dbConfig.insertSeleniumGridImages();
            log.info("{} are inserted successfully. Permission count post insertion is:{} ", name, list.size());
        }else {
            log.info("{} are available in DB with count:{} ", name, all.size());
        }
    }

    private void assertTools() throws IOException {
        toolsData.checkTools();
    }

    private void assertProjects() {
        //Need to implement the logic for this
    }

    private void assertRoles() {
        //Need to implement the logic for this
    }

    private void assertUser() {
        //Need to implement the logic for this
    }



}