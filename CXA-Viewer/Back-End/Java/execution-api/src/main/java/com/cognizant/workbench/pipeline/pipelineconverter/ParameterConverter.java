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

package com.cognizant.workbench.pipeline.pipelineconverter;

import com.cognizant.workbench.error.ThrowException;
import com.cognizant.workbench.pipeline.model.Constants;
import com.cognizant.workbench.pipeline.model.directives.Parameter;
import com.cognizant.workbench.pipeline.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ParameterConverter {
    private static final String PARAM = "%s" + "parameters{%n";
    private static final String STRING_PARAM = "%s" + "string(name: '%s', defaultValue: '%s', description: '%s')%n";
    private static final String TEXT_PARAM = "%s" + "text(name: '%s', defaultValue: '%s', description: '%s')%n";
    private static final String BOOLEAN_PARAM = "%s" + "booleanParam(name: '%s', defaultValue: %s, description: '%s')%n";
    private static final String CHOICE_PARAM = "%s" + "choice(name: '%s', choices: %s, description: '%s')%n";
    private static final String PASS_PARAM = "%s" + "password(name: '%s', defaultValue: '%s', description: '%s')%n";
    private static final String FILE_PARAM = "%s" + "file(name: '%s', description: '%s')%n";

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    String convert(List<Parameter> parameterList, int i) {
        log.info("convert() : Started");
        if (CollectionUtils.isEmpty(parameterList)) return "";
        final StringBuilder stringBuilder = new StringBuilder();
        for (Parameter parameter : parameterList) {
            switch (Objects.toString(parameter.getParamType(), "")) {
                case "string":
                    stringBuilder.append(String.format(STRING_PARAM, tabs(i + 1)
                            , Objects.toString(parameter.getName(), "")
                            , Objects.toString(parameter.getDefaultValue(), "")
                            , Objects.toString(parameter.getDescription(), "")));
                    break;
                case "text":
                    stringBuilder.append(String.format(TEXT_PARAM, tabs(i + 1)
                            , Objects.toString(parameter.getName(), "")
                            , Objects.toString(parameter.getDefaultValue(), "")
                            , Objects.toString(parameter.getDescription(), "")));
                    break;
                case "booleanParam":
                    stringBuilder.append(String.format(BOOLEAN_PARAM, tabs(i + 1)
                            , Objects.toString(parameter.getName(), "")
                            , Objects.toString(parameter.getDefaultValue(), "")
                            , Objects.toString(parameter.getDescription(), "")));
                    break;
                case "choice":
                    List<String> strings = parameter.getChoices();
                    stringBuilder.append(String.format(CHOICE_PARAM, tabs(i + 1)
                            , Objects.toString(parameter.getName(), "")
                            , !parameter.getChoices().isEmpty() ?
                                    "[" + String.join(",", strings.stream().map(choice -> ("'" + choice + "'")).collect(Collectors.toList())) + "]" : ""
                            , Objects.toString(parameter.getDescription(), "")));
                    break;
                case "password":
                    stringBuilder.append(String.format(PASS_PARAM, tabs(i + 1)
                            , Objects.toString(parameter.getName(), "")
                            , Objects.toString(parameter.getDefaultValue(), "")
                            , Objects.toString(parameter.getDescription(), "")));
                    break;
                case "file":
                    stringBuilder.append(String.format(FILE_PARAM, tabs(i + 1)
                            , Objects.toString(parameter.getName(), "")
                            , Objects.toString(parameter.getDescription(), "")));
                    break;
                default:
                    break;
            }

        }

        if (StringUtils.isEmpty(stringBuilder.toString())){
            return "";
        }else {
            stringBuilder.insert(0, String.format(PARAM, tabs(i)));
            stringBuilder.append(String.format("%s}%n", tabs(i)));
        }
        log.info("convert() : Ended");
        return stringBuilder.toString();
    }

    public String xmlConverter(List<Parameter> parameterList) {
        String returnString = "";
        DocumentBuilderFactory dbFactory = Util.getDocumentBuilderFactory();
        DocumentBuilder dBuilder = null;
        Document doc ;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();

            Element properties = doc.createElement(Constants.PROPERTIES_TAG);
            doc.appendChild(properties);
            Element parameters = doc.createElement(Constants.PARAMETERS_DEFINITION_PROPERTY_TAG);
            properties.appendChild(parameters);
            Element parametersDef = doc.createElement(Constants.PARAMETER_DEFINITIONS_TAG);
            parameters.appendChild(parametersDef);

            for (Parameter parameter : parameterList) {
                Element docElement;
                switch (parameter.getParamType()) {
                    case "string":
                        docElement = Util.generateParameterElement(doc, Constants.STRING_PARAMETER_DEFINITION_TAG, parameter.getName(), parameter.getDescription(), parameter.getDefaultValue(), null);
                        parametersDef.appendChild(docElement);
                        break;
                    case "text":
                        docElement = Util.generateParameterElement(doc, Constants.TEXT_PARAMETER_DEFINITION_TAG, parameter.getName(), parameter.getDescription(), parameter.getDefaultValue(), null);
                        parametersDef.appendChild(docElement);
                        break;
                    case "booleanParam":
                        docElement = Util.generateParameterElement(doc, Constants.BOOLEAN_PARAMETER_DEFINITION_TAG, parameter.getName(), parameter.getDescription(), parameter.getDefaultValue(), null);
                        parametersDef.appendChild(docElement);
                        break;
                    case "choice":
                        docElement = Util.generateParameterElement(doc, Constants.CHOICE_PARAMETER_DEFINITION_TAG, parameter.getName(), parameter.getDescription(), null, parameter.getChoices());
                        parametersDef.appendChild(docElement);
                        break;
                    case "password":
                        docElement = Util.generateParameterElement(doc, Constants.TOKEN_PARAMETER_DEFINITION_TAG, parameter.getName(), parameter.getDescription(), parameter.getDefaultValue(), null);
                        parametersDef.appendChild(docElement);
                        break;
                    case "file":
                        docElement = Util.generateParameterElement(doc, Constants.FILE_PARAMETER_DEFINITION_TAG, parameter.getName(), parameter.getDescription(), null, null);
                        parametersDef.appendChild(docElement);
                        break;
                    default:
                        break;
                }

            }

            // write the content into xml file
            TransformerFactory transformerFactory = Util.getTransformerFactory();
            Transformer transformer = transformerFactory.newTransformer();

            // Beautify the format of the resulted XML
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // to omit <?xml> tag from the result xml
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            DOMSource source = new DOMSource(doc);
            /**StreamResult result = new StreamResult(new File("C:\\Users\\784420\\RAM\\config.xml"));
            transformer.transform(source, result);*/

            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(source, streamResult);
            returnString = writer.toString();

            /**
            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
             */

        } catch (ParserConfigurationException | TransformerException e) {
            log.error(e.getLocalizedMessage());
            throw new ThrowException("Error while parsing Parameter xml", e);
        }
        return returnString;
    }
}
