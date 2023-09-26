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

package com.cognizant.workbench.pipeline.model.snippets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Created by 784420 on 6/13/2019 7:00 PM
 */
@Data
public class Mail {
    private String bcc;
    private String body;
    private String cc;
    private String from;
    private String replyTo;
    private String subject;
    private String to;

    @JsonIgnore
    public String toString() {
        String mail = "mail bcc: '%s', body: '%s', cc: '%s', from: '%s', replyTo: '%s', subject: '%s', to: '%s'";
        return String.format(mail, bcc, body, cc, from, replyTo, subject, to);
    }
}
