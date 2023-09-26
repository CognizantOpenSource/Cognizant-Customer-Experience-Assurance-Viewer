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

package com.cognizant.workbench.init.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 784420 on 8/16/2019 1:26 PM
 */
@Service
@Slf4j
public class InitDBUtil {

    public byte[] getBytes(String jsonFilePath) throws IOException {
        byte[] buffer;
        int count ;
        try (InputStream inputStream = getClass().getResourceAsStream(jsonFilePath)) {
            buffer = new byte[inputStream.available()];
            count = inputStream.read(buffer);
        } catch (IOException e) {
            log.error("Exception while inserting SourceStage in DB", e);
            throw e;
        }
        if (count > 0) return buffer;
        else return new byte[0];
    }

}
