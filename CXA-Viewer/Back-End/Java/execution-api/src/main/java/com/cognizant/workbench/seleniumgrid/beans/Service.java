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

package com.cognizant.workbench.seleniumgrid.beans;

public class Service {
    private Service() {
    }
    public static final String SERVICE_TEMPLATE = "### It is Auto Generated docker-compose file from the Application %n" +
            "## Pleas follow below mentioned command to run this docker-compose file %n" +
            "## if file name is docker-compose.yml file --> docker-compose up %n" +
            "## or else --> docker-compose -f <file name.yml> up%n" +
            "version: '3'%n" +
            "services:%n";
    public static final String FIREFOX = "  %s:%n" +
            "    image: selenium/node-firefox:%s%n" +
            "    volumes:%n" +
            "      - /dev/shm:/dev/shm%n" +
            "    depends_on:%n" +
            "      - hub%n" +
            "    environment:%n" +
            "      HUB_HOST: hub%n" +
            "%n";
    public static final String CHROME = "  %s:%n" +
            "    image: selenium/node-chrome:%s%n" +
            "    volumes:%n" +
            "      - /dev/shm:/dev/shm%n" +
            "    depends_on:%n" +
            "      - hub%n" +
            "    environment:%n" +
            "      HUB_HOST: hub%n" +
            "%n";
    public static final String HUB = "  hub:%n" +
            "    image: selenium/hub:%s%n" +
            "    ports:%n" +
            "      - \"4444:4444\"";

    /*version: '2'
    services:
    FIREFOX:
    image: selenium/node-FIREFOX:3.14.0-gallium
    volumes:
            - /dev/shm:/dev/shm
    depends_on:
            - HUB
    environment:
    HUB_HOST: HUB

    CHROME:
    image: selenium/node-CHROME:3.14.0-gallium
    volumes:
            - /dev/shm:/dev/shm
    depends_on:
            - HUB
    environment:
    HUB_HOST: HUB

    HUB:
    image: selenium/HUB:3.14.0-gallium
    ports:
            - "4444:4444"*/
}
