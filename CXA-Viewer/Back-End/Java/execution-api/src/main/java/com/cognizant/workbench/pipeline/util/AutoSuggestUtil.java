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

package com.cognizant.workbench.pipeline.util;

import com.cognizant.workbench.base.config.UserAuditing;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggest;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Created by 784420 on 9/17/2019 1:24 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class AutoSuggestUtil {

    private UserAuditing auditing;

    public AutoSuggest convertDeployDtoToEntity(AutoSuggestDTO dto, AutoSuggest autoSuggest) {
        BeanUtils.copyProperties(dto, autoSuggest);
        return autoSuggest;
    }

    public AutoSuggest convertDeployDtoToNewEntity(AutoSuggestDTO dto, AutoSuggest autoSuggest) {
        BeanUtils.copyProperties(dto, autoSuggest);
        autoSuggest.setCreatedUser(getCurrentUser());
        autoSuggest.setCreatedDate(new Date());
        return autoSuggest;
    }

    private String getCurrentUser() {
        Optional<String> currentAuditor = auditing.getCurrentAuditor();
        return currentAuditor.orElse("");
    }
}
