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

package com.cognizant.workbench.pipeline.parser.utils;

import com.cognizant.workbench.error.InvalidValueException;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipelineUtil {
    private PipelineUtil() {}

    public static boolean isStage(String line) {
        boolean isStage = false;

        if (line.contains(PipelineConstants.STAGE) && line.contains("(") && line.contains(")")) {
            isStage = true;
        }
        return isStage;
    }

    public static boolean isPipelineTag(String line, String pipelineTag) {
        boolean isPipelineTag = false;
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        line = line.trim();
        if (line.contains(pipelineTag)) {
            line = line.replace(" ", "");
            String lastChar = line.substring(line.length() - 1);
            String secondLastChar = Character.toString(line.charAt(line.length() - 2));
            Matcher m = p.matcher(secondLastChar);
            boolean b = m.find();
            if (lastChar.equals("{") && !b) {
                isPipelineTag = true;
            }
        }

        return isPipelineTag;
    }

    public static boolean isBalancedOld(final String str1, final LinkedList<Character> openedBrackets, final Map<Character, Character> closeToOpen) {
        try {
            if ((str1 == null) || str1.isEmpty()) {
                return openedBrackets.isEmpty();
            } else if (closeToOpen.containsValue(str1.charAt(0))) {
                openedBrackets.add(str1.charAt(0));
                return isBalanced(str1.substring(1), openedBrackets, closeToOpen);
            } else if (closeToOpen.containsKey(str1.charAt(0))) {
                if (openedBrackets.getLast().equals(closeToOpen.get(str1.charAt(0)))) {
                    openedBrackets.removeLast();
                    return isBalanced(str1.substring(1), openedBrackets, closeToOpen);
                } else {
                    return false;
                }
            } else {
                return isBalanced(str1.substring(1), openedBrackets, closeToOpen);
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBalanced(final String data, final LinkedList<Character> openedBrackets, final Map<Character, Character> closeToOpen) {
        ArrayList<Map.Entry<Character, Character>> entries = new ArrayList<>(closeToOpen.entrySet());
        Map.Entry<Character, Character> entry = entries.get(0);
        Character close = entry.getKey();
        Character open = entry.getValue();
        Stack<Character> stack = new Stack<>();
        for (char c : data.toCharArray()) {
            if (c == open) {
                stack.push(c);
            } else if (
                    (c == close && (stack.isEmpty() || !stack.pop().equals(open)))) {
                return false;
            }
        }
        return stack.isEmpty();
    }


    public static List<List<String>> splitPipeline(List<String> pipelineRawList) {

        List<List<String>> stagesList = new ArrayList<>();
        List<String> stage = new ArrayList<>();
        try {
            for (String s : pipelineRawList) {

                String dataClean = s.trim();
                stage.add(dataClean);

                if (dataClean.contains("stages")) {
                    stagesList.add(stage);
                    stage = new ArrayList<>();
                }

                if (isStage(dataClean)) {
                    stage.remove(dataClean);
                    stagesList.add(stage);
                    stage = new ArrayList<>();
                    stage.add(dataClean);
                }
            }
            // Handle to add the last Stage Data into List
            if (stage.size() > 1)
                stagesList.add(stage);

        } catch (Exception e) {
            throw new InvalidValueException(" Error while Splitting pipeline into List of Stages ");
        }
        return stagesList;
    }

}
