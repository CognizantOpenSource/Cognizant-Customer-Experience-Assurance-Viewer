<#-- @ftlvariable name="env" type="com.cognizant.leap.model.StageConverter" -->

"environments" : [
    <#list env.getEnvList() as envMap>
       {
          "key" : "${envMap.get("Key")}",
          "value" :
            {
              "type" : "${envMap.get("Type")}",
              "value" : "${envMap.get("Value")}"
            }
       }
       <#if envMap?has_next>
          ,
       </#if>
    </#list>
]