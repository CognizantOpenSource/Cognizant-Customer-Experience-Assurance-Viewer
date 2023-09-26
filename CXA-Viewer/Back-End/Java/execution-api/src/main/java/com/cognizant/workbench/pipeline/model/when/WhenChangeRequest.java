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

package com.cognizant.workbench.pipeline.model.when;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class WhenChangeRequest {
    private String comparator;
    private String author;
    private String authorDisplayName;
    private String authorEmail;
    private String branch;
    private String fork;
    private String id;
    private String target;
    private String title;
    private String url;

    @JsonIgnore
    public String toConvert() {
        List<String> list = new ArrayList<>();

        if (!StringUtils.isEmpty(author))
            list.add("author: '" + author + '\'');
        if (!StringUtils.isEmpty(authorDisplayName))
            list.add("authorDisplayName: '" + authorDisplayName + '\'');
        if (!StringUtils.isEmpty(authorEmail))
            list.add("authorEmail: '" + authorEmail + '\'');
        if (!StringUtils.isEmpty(branch))
            list.add("branch: '" + branch + '\'');
        if (!StringUtils.isEmpty(comparator))
            list.add("comparator: '" + comparator + '\'');
        if (!StringUtils.isEmpty(fork))
            list.add("fork: '" + fork + '\'');
        if (!StringUtils.isEmpty(id))
            list.add("id: '" + id + '\'');
        if (!StringUtils.isEmpty(target))
            list.add("target: '" + target + '\'');
        if (!StringUtils.isEmpty(title))
            list.add("title: '" + title + '\'');
        if (!StringUtils.isEmpty(url))
            list.add("url: '" + url + '\'');

        return "changeRequest " +
                String.join(",", list)
                + "\n";
    }
}
