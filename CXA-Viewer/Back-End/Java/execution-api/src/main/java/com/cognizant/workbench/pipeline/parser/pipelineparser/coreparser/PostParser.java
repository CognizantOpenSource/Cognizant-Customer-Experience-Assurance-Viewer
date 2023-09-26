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

package com.cognizant.workbench.pipeline.parser.pipelineparser.coreparser;

import com.cognizant.workbench.pipeline.parser.model.PipelinePost;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PostParser {

    public PipelinePost getPost(List<String> postList) {

        PipelinePost post = new PipelinePost();
        CopyOnWriteArrayList<String> finalClassNames = new CopyOnWriteArrayList<>(postList);

        if (CollectionUtils.isEmpty(finalClassNames)) return post;
        if (finalClassNames.get(0).equals("}")) finalClassNames.remove(0);
        finalClassNames.removeAll(Collections.singleton(""));
        finalClassNames.removeIf(String::isEmpty);

        final Map<Character, Character> closeToOpen = Collections.singletonMap('}', '{');

        removeCloseBrackets(finalClassNames, closeToOpen);
        removePostTag(finalClassNames);

        List<Map<String, String>> postDetailedList = new ArrayList<>();
        int j = 0;
        while (j < finalClassNames.size()) {
            String line = finalClassNames.get(j);
            if (PipelineUtil.isPipelineTag(line, PipelineConstants.ALWAYS)) {
                Map<String, String> alwaysMap = new HashMap<>();
                List<String> alwaysList = new ArrayList<>();
                int cnt = j + 1;
                cnt = postAlways(finalClassNames, alwaysList, closeToOpen, cnt);

                alwaysMap.put("Type", "always");
                alwaysMap.put("Script", alwaysList.stream()
                        .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                        .collect(Collectors.joining(",\n ")));
                postDetailedList.add(alwaysMap);
                j = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.SUCCESS)) {
                Map<String, String> successMap = new HashMap<>();
                List<String> successList = new ArrayList<>();
                int cnt = j + 1;
                cnt = postSuccess(finalClassNames, successList, closeToOpen, cnt);
                successMap.put("Type", "success");
                successMap.put("Script", successList.stream()
                        .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                        .collect(Collectors.joining(",\n ")));
                postDetailedList.add(successMap);
                j = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.FAILURE)) {
                Map<String, String> failureMap = new HashMap<>();
                List<String> failureList = new ArrayList<>();
                int cnt = j + 1;
                cnt = postFailure(finalClassNames, failureList, closeToOpen, cnt);
                failureMap.put("Type", "failure");
                failureMap.put("Script", failureList.stream()
                        .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                        .collect(Collectors.joining(",\n ")));
                postDetailedList.add(failureMap);
                j = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.CHANGED)) {
                Map<String, String> changedMap = new HashMap<>();
                List<String> changedList = new ArrayList<>();
                int cnt = j + 1;
                cnt = postChanged(finalClassNames, changedList, closeToOpen, cnt);

                changedMap.put("Type", "changed");
                changedMap.put("Script", changedList.stream()
                        .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                        .collect(Collectors.joining(",\n ")));
                postDetailedList.add(changedMap);
                j = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.UNSTABLE)) {
                Map<String, String> unstableMap = new HashMap<>();
                List<String> unstableList = new ArrayList<>();
                int cnt = j + 1;
                cnt = postUnstable(finalClassNames, unstableList, closeToOpen, cnt);

                unstableMap.put("Type", "unstable");
                unstableMap.put("Script", unstableList.stream()
                        .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                        .collect(Collectors.joining(",\n ")));
                postDetailedList.add(unstableMap);
                j = cnt - 1;
            } else {
                post.getPostUnhandled().add(line);
            }
            j++;
        }

        post.setPostList(postDetailedList);

        post.setScript(finalClassNames.stream()
                .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                .collect(Collectors.joining(",\n ")));

        return post;
    }

    private void removePostTag(CopyOnWriteArrayList<String> finalClassNames) {
        if (PipelineUtil.isPipelineTag(finalClassNames.get(0), PipelineConstants.POST)) {
            finalClassNames.remove(0);
            finalClassNames.remove(finalClassNames.size() - 1);
        }
    }

    private int postAlways(CopyOnWriteArrayList<String> finalClassNames, List<String> alwaysList, Map<Character, Character> closeToOpen, int cnt) {
        while ((!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SUCCESS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.FAILURE))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.CHANGED))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.UNSTABLE))) {

            if (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SCRIPT)) {
                alwaysList.add(finalClassNames.get(cnt));
            }
            cnt++;
            if (cnt == finalClassNames.size()) {
                break;
            }
        }
        removeCloseBrackets(alwaysList, closeToOpen);
        return cnt;
    }

    private int postSuccess(CopyOnWriteArrayList<String> finalClassNames, List<String> successList, Map<Character, Character> closeToOpen, int cnt) {
        while ((!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.ALWAYS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.FAILURE))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.CHANGED))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.UNSTABLE))) {

            if (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SCRIPT)) {
                successList.add(finalClassNames.get(cnt));
            }
            cnt++;
            if (cnt == finalClassNames.size()) {
                break;
            }
        }
        removeCloseBrackets(successList, closeToOpen);
        return cnt;
    }

    private int postFailure(CopyOnWriteArrayList<String> finalClassNames, List<String> failureList, Map<Character, Character> closeToOpen, int cnt) {
        while ((!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.ALWAYS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SUCCESS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.CHANGED))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.UNSTABLE))) {

            if (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SCRIPT)) {
                failureList.add(finalClassNames.get(cnt));
            }
            cnt++;
            if (cnt == finalClassNames.size()) {
                break;
            }
        }
        removeCloseBrackets(failureList, closeToOpen);
        return cnt;
    }

    private int postChanged(CopyOnWriteArrayList<String> finalClassNames, List<String> changedList, Map<Character, Character> closeToOpen, int cnt) {
        while ((!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.ALWAYS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SUCCESS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.FAILURE))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.UNSTABLE))) {

            if (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SCRIPT)) {
                changedList.add(finalClassNames.get(cnt));
            }
            cnt++;
            if (cnt == finalClassNames.size()) {
                break;
            }
        }

        removeCloseBrackets(changedList, closeToOpen);
        return cnt;
    }

    private int postUnstable(CopyOnWriteArrayList<String> finalClassNames, List<String> unstableList, Map<Character, Character> closeToOpen, int cnt) {

        while ((!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.ALWAYS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SUCCESS))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.CHANGED))
                && (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.FAILURE))) {

            if (!PipelineUtil.isPipelineTag(finalClassNames.get(cnt), PipelineConstants.SCRIPT)) {
                unstableList.add(finalClassNames.get(cnt));
            }
            cnt++;
            if (cnt == finalClassNames.size()) {
                break;
            }
        }

        removeCloseBrackets(unstableList, closeToOpen);
        return cnt;
    }

    private void removeCloseBrackets(List<String> list, Map<Character, Character> closeToOpen) {
        String joinStr = String.join("", list);
        if (!PipelineUtil.isBalanced(joinStr, new LinkedList<>(), closeToOpen)) {
            int i = list.size() - 1;
            if (!CollectionUtils.isEmpty(list) && list.get(i).equals("}")) list.remove(list.get(i));
        }

    }

}
