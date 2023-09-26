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
import com.cognizant.workbench.init.util.InitDBUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class TemplateManager {

    private final Configuration freemarkerConfig;
    //    private static final String TEMPLATE_DIRECTORY = Util.getResourcePath() + Util.getFileSeparator() + "pipeline-template" + Util.getFileSeparator();
    private static final String TEMPLATE_DIRECTORY = "/pipeline-template/";
    private InitDBUtil util = new InitDBUtil();

    public TemplateManager() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_30);
        freemarkerConfig.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setNumberFormat("computer");
        freemarkerConfig.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_30).build());
        freemarkerConfig.setTemplateLoader(new StringTemplateLoader());
    }

    private Template loadTemplate(String templateName, String templatePath) {
        try {
//            String templateContent = new String(Files.readAllBytes(Paths.get(templatePath)));
            String templateContent = new String(util.getBytes(templatePath));
            ((StringTemplateLoader) freemarkerConfig.getTemplateLoader()).putTemplate(templateName, templateContent);
            return freemarkerConfig.getTemplate(templateName);
        } catch (IOException e) {
            throw new InvalidValueException(e.getLocalizedMessage(), e);
        }
    }

    public String processTemplate(String templateName, Map<String, Object> data) {
        log.info("Processing the template " + templateName + ".ftl");
        Template template = loadTemplate(templateName, TEMPLATE_DIRECTORY + templateName + ".ftl");
        try (StringWriter writer = new StringWriter()) {
            template.process(data, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("Error while processing the template " + templateName + ".ftl" + e.getMessage());
            throw new InvalidValueException(e.getLocalizedMessage(), e);
        }
    }
}
