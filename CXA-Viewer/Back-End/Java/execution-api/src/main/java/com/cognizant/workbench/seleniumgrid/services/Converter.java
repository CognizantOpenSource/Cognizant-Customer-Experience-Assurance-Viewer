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

package com.cognizant.workbench.seleniumgrid.services;

import com.cognizant.workbench.seleniumgrid.beans.Image;
import com.cognizant.workbench.seleniumgrid.beans.Service;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    private static final String CHROME_LATEST = "latest";
    private static final String FIREFOX_LATEST = "latest";
    private static final String HUB_LATEST = "latest";

    private List<String> list = new ArrayList<>();
    private int i = 1;
    private int j = 1;

    String convert(List<Image> images) {

        StringBuilder stringBuilder = new StringBuilder().append(String.format(Service.SERVICE_TEMPLATE));
        for (Image image : images) {
            switch (image.getBrowser()) {
                case "chrome":
                    stringBuilder.append(convertChrome(image));
                    break;
                case "firefox":
                    stringBuilder.append(convertFirefox(image));
                    break;
                default:
                    break;
            }
        }
        stringBuilder.append(String.format(Service.HUB, HUB_LATEST));
        return stringBuilder.toString();
    }

    private String convertChrome(Image image) {
        String imageName = !image.getDocker_version().equals("") ? image.getDocker_version() : CHROME_LATEST;
        String name = "chrome_" + (!image.getVersion().contains("\\.") ? image.getVersion() : image.getVersion().split("\\.")[0]);
        if (!list.contains(name)) list.add(name);
        else name += i++;
        return String.format(Service.CHROME, name, imageName);
    }

    private String convertFirefox(Image image) {
        String imageName = !image.getDocker_version().equals("") ? image.getDocker_version() : FIREFOX_LATEST;
        String name = "firefox_" + (!image.getVersion().contains("\\.") ? image.getVersion() : image.getVersion().split("\\.")[0]);
        if (!list.contains(name)) list.add(name);
        else name += j++;
        return String.format(Service.FIREFOX, name, imageName);
    }
}
