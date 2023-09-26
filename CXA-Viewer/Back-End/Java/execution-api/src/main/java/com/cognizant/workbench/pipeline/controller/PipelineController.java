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

package com.cognizant.workbench.pipeline.controller;

import com.cognizant.workbench.pipeline.model.PipelineConfigBean;
import com.cognizant.workbench.pipeline.dto.ProjectDTO;
import com.cognizant.workbench.pipeline.parser.GenerateJson;
import com.cognizant.workbench.pipeline.service.PipelineService;
import com.cognizant.workbench.pipeline.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/pipeline")
@Slf4j
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;
    @Autowired
    private GenerateJson generateJson;

    /**
     * Generating jenkins pipeline based on the provided project details
     *
     * @param project  project details, based on this will generate the jenkins pipeline script
     * @param type     Tools type (As of now jenkins)
     * @param version  version numbers
     * @param platform os platform (Windows or linux as of now)
     * @return generated pipeline script
     */
    @PostMapping(value = "/convert", name = "pipelineConverter")
    @PreAuthorize("hasPermission('Pipeline','pipeline.convert')")
    public ResponseEntity<Object> pipelineConverter(@RequestBody ProjectDTO project,
                                                    @RequestParam String type, @RequestParam String version, @RequestParam(defaultValue = "windows") String platform) {
        log.info("Getting Converted pipeline using POST method");
        PipelineConfigBean configBean = new PipelineConfigBean(type, version, project.getPlatform());
        Object object = pipelineService.generatePipeline(Util.mapDtoToDocument(project), configBean);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand("Success - ").toUri();

        return ResponseEntity.created(location).header("RespondedController", "PipelineController").body(object);
    }

    @PostMapping("/to-json")
    @PreAuthorize("hasPermission('Pipeline','pipeline.convert')")
    public Map<String, Object> pipelineConvertToJson(@RequestBody String pipelineData){
        String jsonString = generateJson.generateJsonString(pipelineData);
        return new JSONObject(jsonString).toMap();
    }
}
