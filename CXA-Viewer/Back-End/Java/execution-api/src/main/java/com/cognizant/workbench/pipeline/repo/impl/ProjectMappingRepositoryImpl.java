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

package com.cognizant.workbench.pipeline.repo.impl;

import com.cognizant.workbench.pipeline.model.ProjectMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectMappingRepositoryImpl {
    @Autowired
    MongoTemplate template;

    public void updateWithTeams(String teamId, List<String> projectIds) {
        Query query = Query.query(Criteria.where("_id").in(projectIds));
        Update update = new Update();
        update.addToSet("teamIds", teamId);
        template.updateMulti(query, update, ProjectMapping.class);
    }

    public void removeWithTeams(String projectId, String teamId) {
        Query query = Query.query(Criteria.where("_id").is(projectId));
        Update update = new Update();
        update.pull("teamIds", teamId);
        template.updateFirst(query, update, ProjectMapping.class);
    }
}
