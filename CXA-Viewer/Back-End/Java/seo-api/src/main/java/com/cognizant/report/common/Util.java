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

package com.cognizant.report.common;

import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Util {

    private static final List<Pattern> PATTERNS = Arrays.asList(Pattern.compile("(.*?) expected: <(.*?)> but was: <(.+?)>", Pattern.MULTILINE),
            Pattern.compile("(.*?) expected: \\[(.*?)\\] but was: \\[(.+?)\\]", Pattern.MULTILINE),
            Pattern.compile("(.*?) expected \\[(.+?)\\] but found \\[(.+?)\\]", Pattern.MULTILINE));

    private Util() {
    }


    @Data
    public static class DateAndDuration {
        private Date startTime;
        private Date endTime;
        private long duration;
    }


}
