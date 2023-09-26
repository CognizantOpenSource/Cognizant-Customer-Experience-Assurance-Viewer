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

package com.cognizant.workbench.db.dal;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by 784420 on 11/21/2019 12:04 PM
 */
@Component
@Slf4j
public class MongoTemplateImpl {

    @Autowired
    private MongoTemplate template;

    public long updateSingleFieldAllDocuments(@NotBlank Class<?> clazzName,
                                              @Nullable String whereKey, @Nullable Object whereValue,
                                              @NotBlank String setKey, @NotNull Object setValue){
        Query query = new Query();
        if (!StringUtils.isEmpty(whereKey) && Objects.nonNull(whereValue)){
            query.addCriteria(Criteria.where(whereKey).is(whereValue));
        }
        Update update = new Update();
        update.set(setKey, setValue);
        UpdateResult updateResult = template.updateMulti(query, update, clazzName);
        return updateResult.getModifiedCount();
    }
}
