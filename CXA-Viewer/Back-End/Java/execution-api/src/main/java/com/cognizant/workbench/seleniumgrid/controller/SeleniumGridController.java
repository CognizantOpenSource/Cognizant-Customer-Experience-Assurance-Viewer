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

package com.cognizant.workbench.seleniumgrid.controller;

import com.cognizant.workbench.seleniumgrid.beans.Image;
import com.cognizant.workbench.seleniumgrid.services.DockerComposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docker/grid")
public class SeleniumGridController {

    @Autowired
    private DockerComposeService dockerComposeService;

    /**
     * Generating Docker compose file for the SeleniumGrid
     * @param images List of images, Based on this will generate compose file
     * @return Docker compose file
     */
    @PostMapping(value = "/generate", consumes = "application/json", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission('SeleniumGrid','selenium.docker-compose.generate')")
    public String generateScript(@RequestBody List<Image> images) {
        return dockerComposeService.generateCompose(images);
    }

    /**
     * Getting list of images which are available (as of now static data in the application of images.json file)
     * @return list of images
     */
    @GetMapping("/images")
    @PreAuthorize("hasPermission('SeleniumGrid','selenium.docker.images')")
    public List<Image> getImages() {
        return dockerComposeService.getImagesList();
    }

}
