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

package com.cognizant.workbench.pipeline.service;

import com.cognizant.workbench.db.dal.MongoTemplateImpl;
import com.cognizant.workbench.error.ResourceNotFoundException;
import com.cognizant.workbench.pipeline.dto.AppStatusDTO;
import com.cognizant.workbench.pipeline.model.AppStatus;
import com.cognizant.workbench.pipeline.repo.AppStatusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 11/20/2019 7:00 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class AppStatusService {

    private AppStatusRepository repository;
    private MongoTemplateImpl templateImpl;

    public AppStatus get(String name) {
        return assertAndGet(name);
    }

    public Optional<AppStatus> getOptional(String name) {
        return repository.findById(name);
    }

    public List<AppStatus> getAll() {
        return repository.findAll();
    }

    public AppStatus update(AppStatusDTO dto) {
        AppStatus appStatus = new AppStatus();
        BeanUtils.copyProperties(dto, appStatus);
        return repository.save(appStatus);
    }

    public void delete(String name) {
        assertAndGet(name);
        repository.deleteById(name);
    }

    public Object refreshAll(Boolean isRefresh) {
        long updatedCount = templateImpl.updateSingleFieldAllDocuments(AppStatus.class, null, null, "refresh", isRefresh);
        return Collections.singletonMap("updatedCount" , updatedCount);
    }

    public AppStatus refreshByName(String name, Boolean isRefresh) {
        AppStatus appStatus = assertAndGet(name);
        appStatus.setRefresh(isRefresh);
        return repository.save(appStatus);
    }

    private AppStatus assertAndGet(String name){
        return repository.findById(name)
                .orElseThrow(
                        () -> new ResourceNotFoundException("ApplicationOrCollector", "name", name)
                );
    }
}
