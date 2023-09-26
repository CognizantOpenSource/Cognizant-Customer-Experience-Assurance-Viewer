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

import java.io.File;
import java.nio.file.Paths;

/**
 * Class to encapsulate utility functions of the framework
 *
 * @author Cognizant
 */
public class Util {

    private Util() {
        // To prevent external instantiation of this class
    }

    /**
     * Function to get the separator string to be used for directories and files
     * based on the current OS
     *
     * @return The file separator string
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * Function to get the Absolute Path
     *
     * @return The AbsolutePath in String
     */
    public static String getAbsolutePath() {
        //return new File(System.getProperty("user.dir")).getAbsolutePath();  //SAST-Scan Issue Fix
        return Paths.get(System.getProperty("user.dir")).toFile().getAbsolutePath();
    }

    /**
     * Function to get the separator string to be used for directories and files
     * based on the current Framework
     *
     * @return The file separator string for to get Resource Path
     */
    public static String getResourcePath() {

        return getAbsolutePath() + getFileSeparator() + "src" + getFileSeparator() + "main" + getFileSeparator() + "resources";

    }

}