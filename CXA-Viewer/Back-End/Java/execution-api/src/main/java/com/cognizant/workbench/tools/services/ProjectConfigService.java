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

package com.cognizant.workbench.tools.services;

import com.cognizant.workbench.error.ResourceNotFoundException;
import com.cognizant.workbench.tools.beans.ToolProjectConfig;
import com.cognizant.workbench.tools.repo.ProjectConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 11/7/2019 2:21 PM
 */
@Slf4j
@Service
@AllArgsConstructor
public class ProjectConfigService {

    private ProjectConfigRepository repository;

    public List<ToolProjectConfig> getAll() {
        return repository.findAll();
    }

    public ToolProjectConfig get(String id) {
        return assertOrGet(id);
    }

    public ToolProjectConfig create(ToolProjectConfig projectConfig) {
        return repository.insert(projectConfig);
    }

    public ToolProjectConfig update(ToolProjectConfig projectConfig) {
        assertOrGet(projectConfig.getName());
        return repository.save(projectConfig);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private ToolProjectConfig assertOrGet(String id) {
        Optional<ToolProjectConfig> optional = repository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new ResourceNotFoundException("ProjectConfig", "Id", id);
        }
    }

    public List<ToolProjectConfig> addAll(List<ToolProjectConfig> projectConfigList) {
        return repository.insert(projectConfigList);
    }
}
