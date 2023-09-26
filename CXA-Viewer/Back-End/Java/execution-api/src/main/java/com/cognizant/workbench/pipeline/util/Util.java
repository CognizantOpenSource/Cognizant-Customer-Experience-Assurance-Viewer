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

package com.cognizant.workbench.pipeline.util;

import com.cognizant.workbench.jenkins.model.JenkinsScriptConstants;
import com.cognizant.workbench.pipeline.dto.ProjectDTO;
import com.cognizant.workbench.pipeline.model.Constants;
import com.cognizant.workbench.pipeline.model.Project;
import feign.Response;
import feign.gson.GsonDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by 784420 on 7/3/2019 3:29 PM
 */
@Slf4j
public class Util {

    private Util() {
    }

    public static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    public static String joinListWithCommaQuote(List<String> strings) {
        return "'" + String.join("','", strings) + "'";
    }

    public static String readConfigXML() {
        return Constants.CONFIG_XML;
    }

    public static Object decode(Response response, Type type) throws IOException {
        if (response.body() == null) return null;
        return new GsonDecoder().decode(response, type);
    }

    public static Project mapDtoToDocument(ProjectDTO dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        return project;
    }

    public static String generateJenkinsScript(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder(String.format(JenkinsScriptConstants.JENKINS_BASE_SCRIPT));
        map.forEach(
                (key, value) -> stringBuilder.append(String.format(JenkinsScriptConstants.JENKINS_ADD_ENV_SCRIPT, key, value))
        );
        return stringBuilder.toString();
    }

    public static String genJenkinsEnvRemoveScript(String key) {
        StringBuilder stringBuilder = new StringBuilder(String.format(JenkinsScriptConstants.JENKINS_BASE_SCRIPT));
        stringBuilder.append(String.format(JenkinsScriptConstants.JENKINS_REMOVE_ENV_SCRIPT, key));
        return stringBuilder.toString();
    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory documentBuilderFactory = null;
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            /* below two lines included for the SAST remediation
               but this will cause 500 error while updateJob
               Commenting below lines for the workaround
             */
            //documentBuilderFactory.setAttribute(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);//Remediation
            //documentBuilderFactory.setAttribute(XMLInputFactory.SUPPORT_DTD, false); //Remediation
        } catch (ParserConfigurationException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return documentBuilderFactory;
    }

    public static TransformerFactory getTransformerFactory() {
        TransformerFactory factory = null;
        try {
            factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (TransformerConfigurationException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return factory;
    }

    public static String getUpdatedConfigXml(String pipeline, String updateRawXml) {
        String returnString = "";
        try {
            Document document = getDocumentBuilderFactory().newDocumentBuilder().parse(new InputSource(new StringReader(updateRawXml)));

            Node scriptNode = document.getElementsByTagName("script").item(0);
            scriptNode.setTextContent(pipeline);

            DOMSource source = new DOMSource(document);
            TransformerFactory factory = getTransformerFactory();

            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            returnString = writer.toString();
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            log.error("Exception while parsing UpdateConfigXml data: " + e.getLocalizedMessage(), e);
        }
        return returnString;
    }

    public static Element generateParameterElement(@NotNull Document document, @NotBlank String elementName, @NotBlank String name, @NotBlank String description, @Nullable String defaultValue, @Nullable List<String> choices) {
        Element docElement = document.createElement(elementName);

        Element nameElement = document.createElement(Constants.NAME_TAG);
        nameElement.setTextContent(name);
        docElement.appendChild(nameElement);

        Element descriptionElement = document.createElement(Constants.DESCRIPTION_TAG);
        descriptionElement.setTextContent(description);
        docElement.appendChild(descriptionElement);

        if (!StringUtils.isEmpty(defaultValue)) {
            Element defaultValueElement = document.createElement(Constants.DEFAULT_VALUE_TAG);
            defaultValueElement.setTextContent(defaultValue);
            docElement.appendChild(defaultValueElement);
        }

        if (!CollectionUtils.isEmpty(choices)) {
            Element choicesElement = document.createElement(Constants.CHOICES_TAG);
            choices.forEach(s -> {
                Element string = document.createElement(Constants.STRING_TAG);
                string.setTextContent(s);
                choicesElement.appendChild(string);
            });
            docElement.appendChild(choicesElement);
        }

        return docElement;
    }

    public static String getUpdatedConfigXmlValue(String inputXML, String configXML, String tagName) {
        String returnString = "";
        try {
            Document inputDocument = getDocumentBuilderFactory().newDocumentBuilder().parse(new InputSource(new StringReader(inputXML)));
            Document configDocument = getDocumentBuilderFactory().newDocumentBuilder().parse(new InputSource(new StringReader(configXML)));

            Node inputNode = inputDocument.getElementsByTagName(tagName).item(0);
            Node configNode = configDocument.getElementsByTagName(tagName).item(0);

            Node importNode = configDocument.importNode(inputNode, true);

            Element root = configDocument.getDocumentElement();
            root.replaceChild(importNode, configNode);


            DOMSource source = new DOMSource(configDocument);
            TransformerFactory factory = getTransformerFactory();

            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            returnString = writer.toString();
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            log.error("Exception while parsing UpdateConfigXml data: " + e.getLocalizedMessage(), e);
        }
        return returnString;
    }

    public static String getColour(String type) {
        String colour;
        switch (type) {
            case "WARN":
                colour = "#9F6000";
                break;
            case "FAILURE":
                colour = "#FF0000";
                break;
            case "SUCCESS":
                colour = "#00FF00";
                break;
            case "INFO":
            default:
                colour = "#00529B";
                break;
        }
        return colour;
    }
}
