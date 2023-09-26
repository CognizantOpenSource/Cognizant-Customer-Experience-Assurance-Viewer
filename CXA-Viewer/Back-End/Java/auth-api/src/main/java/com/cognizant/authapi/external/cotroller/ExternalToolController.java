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

package com.cognizant.authapi.external.cotroller;

import com.cognizant.authapi.external.beans.ExternalToolConfig;
import com.cognizant.authapi.external.beans.ExternalToolDetails;
import com.cognizant.authapi.external.service.ExternalToolService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 784420 on 6/2/2021 3:26 AM
 */
@RestController
@RequestMapping("/external-tool")
@AllArgsConstructor
@Slf4j
public class ExternalToolController {
    private ExternalToolService service;

    @GetMapping
    public List<ExternalToolDetails> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ExternalToolDetails get(@PathVariable String id) {
        return service.assertOrGet(id);
    }

    @PostMapping
    public ExternalToolDetails add(@RequestBody ExternalToolDetails details) {
        return service.add(details);
    }

    @PutMapping
    public ExternalToolDetails update(@RequestBody ExternalToolDetails details) {
        return service.update(details);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable String id) {
        service.deleteById(id);
    }

    @GetMapping("/current-user")
    public ExternalToolConfig getConfig(@RequestParam("type") ExternalToolConfig.XToolType type) {
        return service.getCurrentUserConfig(type);
    }

    @PostMapping("/current-user")
    @Validated
    public ExternalToolDetails updateConfig(@Valid @RequestBody ExternalToolConfig config) {
        return service.updateCurrentUserConfig(config, config.getType());
    }
}
