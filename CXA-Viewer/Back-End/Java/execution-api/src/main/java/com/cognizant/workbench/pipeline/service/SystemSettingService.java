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

import com.cognizant.workbench.base.services.WhiteListService;
import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.pipeline.model.BaseConstants;
import com.cognizant.workbench.pipeline.model.SystemSetting;
import com.cognizant.workbench.pipeline.repo.SystemSettingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 784420 on 9/27/2019 6:27 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class SystemSettingService {

    private SystemSettingRepository repository;
    private WhiteListService whitelist;

    public List<SystemSetting> getAll() {
        return repository.findAll();
    }

    public SystemSetting getById(String id) {
        return assertAndGet(id);
    }

    private SystemSetting assertAndGet(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ThrowException(String.format(BaseConstants.SYSTEM_SETTINGS_ARE_NOT_FOUND, id)));
    }

    public SystemSetting update(SystemSetting systemSetting) {
        return repository.save(systemSetting);
    }

    public List<SystemSetting> addAll(List<SystemSetting> systemSettingList) {
        return repository.saveAll(systemSettingList);
    }
    public void reloadWhitelist(){
        whitelist.reload();
    }
}
