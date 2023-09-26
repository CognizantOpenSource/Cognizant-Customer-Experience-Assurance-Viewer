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

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {
    private static final List<String> ignoreStrings = Arrays.asList("java.base", "org.openqa.selenium", "net.serenitybdd",
            "net.thucydides.core", "org.junit", "org.gradle", "com.sun", "idea.debugger", "org.testng",
            "jdk.internal.reflect", "com.cognizant.framework");

    private static final List<Pattern> PATTERNS = Arrays.asList(Pattern.compile("(.*?) expected: <(.*?)> but was: <(.+?)>", Pattern.MULTILINE),
            Pattern.compile("(.*?) expected: \\[(.*?)\\] but was: \\[(.+?)\\]", Pattern.MULTILINE),
            Pattern.compile("(.*?) expected \\[(.+?)\\] but found \\[(.+?)\\]", Pattern.MULTILINE));

    private Util() {
    }

    public static DateAndDuration getDurationMinStartMaxEnd(Date startTime1, Date endTime1, Date startTime2, Date endTime2) {
        DateAndDuration dateAndDuration = new DateAndDuration();
        Date minStartTime = minDate(startTime1, startTime2);
        Date maxStartTime = maxDate(startTime1, startTime2);
        Date minEndTime = minDate(endTime1, endTime2);
        Date maxEndTime = maxDate(endTime1, endTime2);

        long duration = ((maxEndTime.getTime() - minStartTime.getTime()) - Math.max((maxStartTime.getTime() - minEndTime.getTime()), 0));
        dateAndDuration.setStartTime(minStartTime);
        dateAndDuration.setEndTime(maxEndTime);
        dateAndDuration.setDuration(duration);
        return dateAndDuration;
    }

    private static Date minDate(Date date1, Date date2) {
        return new Date(Math.min(date1.getTime(), date2.getTime()));
    }

    private static Date maxDate(Date date1, Date date2) {
        return new Date(Math.max(date1.getTime(), date2.getTime()));
    }

    @Data
    public static class DateAndDuration {
        private Date startTime;
        private Date endTime;
        private long duration;
    }

    private static boolean isValidString(String str) {
        Optional<String> optional = ignoreStrings.stream().filter(s -> str.contains(s)).findFirst();
        if (optional.isPresent()) {
            return false;
        }
        return true;
    }

    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order, long limitTo)
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().limit(limitTo).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

}
