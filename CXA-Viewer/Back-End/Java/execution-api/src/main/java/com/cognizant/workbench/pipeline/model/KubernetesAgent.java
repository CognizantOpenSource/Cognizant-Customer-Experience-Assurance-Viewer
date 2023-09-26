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

package com.cognizant.workbench.pipeline.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

@Data
public class KubernetesAgent {
    @NotBlank(message = "Name of the kubernetes should not be blank or null")
    private String name;
    @NotBlank(message = "Image of the kubernetes should not be blank or null")
    private String image;
    private String command;
    private int replicas = 1;
    private int replicaId = 1;
    private String customWorkspace;
    private boolean ttyEnabled;


    @JsonIgnore
    public String toYamlString(){
        return  "spec:\n" +
                "  volumes:\n" +
                "  - name: volume-0\n" +
                "    persistentVolumeClaim: \n" +
                "      claimName: task-pv-claim\n" +
                "      readOnly: false\n" +
                "  containers:\n" +
                "  - name: "+name.toLowerCase()+"\n" +
                "    image: "+image+"\n" +
                "    imagePullPolicy: IfNotPresent\n" +
                (
                    StringUtils.isEmpty(command)
                        ?
                        "    command:\n" +
                        "    - cat\n"
                        :
                        "    command:\n" +
                        String.format("    - %s%n", command)
                )+
                "    tty: "+ ttyEnabled+"\n"+
                "    env: \n"+
                "    - name: REPLICAS\n"+
                "      value: "+ replicas+"\n"+
                "    - name: REPLICA_ID\n"+
                "      value: "+ replicaId+"\n"+
                "    volumeMounts: \n"+
                "    - mountPath: /root/.m2/repository\n"+
                "      name: volume-0\n" +
                "      readOnly: false\n"
                ;
    }
}
