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
import com.cognizant.workbench.seleniumgrid.repo.SeleniumGridImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DockerComposeService {
    private static final String PATH = "classpath:images.json";
//    private List<Image> images;

//    private ObjectMapper objectMapper;
    @Autowired
    SeleniumGridImageRepository imageRepository;

    public DockerComposeService() {

        /*objectMapper = new ObjectMapper();
        JavaType imagesList = objectMapper.getTypeFactory().constructCollectionType(List.class, Image.class);

        File file;
        try {
            file = ResourceUtils.getFile(PATH);
            images = objectMapper.readValue(Files.readAllBytes(file.toPath()), imagesList);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }*/
    }

    public String generateCompose(List<Image> images) {
        Converter converter = new Converter();
        return converter.convert(images);
    }

    public List<Image> getImagesList() {
        return getAll();
    }

    public List<Image> saveAll(List<Image> images){
        return imageRepository.saveAll(images);
    }

    public List<Image> getAll(){
        return imageRepository.findAll();
    }

    public List<Image> addAll(List<Image> images){
        return imageRepository.saveAll(images);
    }
}
