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

package com.cognizant.authapi.external.service;

import com.cognizant.authapi.base.error.ResourceExistsException;
import com.cognizant.authapi.base.error.ResourceNotFoundException;
import com.cognizant.authapi.external.beans.ExternalToolConfig;
import com.cognizant.authapi.external.beans.ExternalToolDetails;
import com.cognizant.authapi.external.repo.ExternalToolRepository;
import com.cognizant.authapi.users.services.UserValidationService;
import com.mongodb.DuplicateKeyException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 6/2/2021 3:11 AM
 */
@Service
@AllArgsConstructor
public class ExternalToolService {

    private ExternalToolRepository repository;
    private UserValidationService userValidationService;

    public List<ExternalToolDetails> getAll() {
        return repository.findAll();
    }

    public Optional<ExternalToolDetails> get(String id) {
        return repository.findById(id);
    }

    public ExternalToolDetails assertOrGet(String id) {
        return get(id).orElseThrow(() -> new ResourceNotFoundException("ExternalToolDetails", "id", id));
    }

    public ExternalToolDetails add(ExternalToolDetails details) {
        try {
            return repository.insert(details);
        } catch (DuplicateKeyException e) {
            throw new ResourceExistsException("ExternalToolDetails", "email", details.getEmail());
        }
    }

    public ExternalToolDetails update(ExternalToolDetails details) {
        return repository.save(details);
    }

    public void deleteById(String id){
        repository.deleteById(id);
    }

    public ExternalToolConfig getCurrentUserConfig(ExternalToolConfig.XToolType type) {
        String emailId = userValidationService.getCurrentUserEmailId();
        ExternalToolDetails details = assertOrGet(emailId);
        Optional<ExternalToolConfig> optional = details.getConfigs().stream().filter(config -> config.getType() == type).findFirst();
        if (optional.isEmpty())  throw new ResourceNotFoundException("ExternalToolConfig", "type", type.name());
        ExternalToolConfig config = optional.get();
        config.setToken(config.getDecodedToken());
        return config;
    }

    public ExternalToolDetails updateCurrentUserConfig(ExternalToolConfig config, ExternalToolConfig.XToolType type) {
        String emailId = userValidationService.getCurrentUserEmailId();
        ExternalToolDetails details = get(emailId).orElse(new ExternalToolDetails(emailId));
        List<ExternalToolConfig> configs = details.getConfigs();
        Optional<ExternalToolConfig> optional = configs.stream().filter(toolConfig -> toolConfig.getType() == type).findFirst();
        optional.ifPresent(xToolConfig -> configs.remove(xToolConfig));
        config.setToken(config.getEncodedToken());
        configs.add(config);
        details.setConfigs(configs);
        return update(details);
    }
}
