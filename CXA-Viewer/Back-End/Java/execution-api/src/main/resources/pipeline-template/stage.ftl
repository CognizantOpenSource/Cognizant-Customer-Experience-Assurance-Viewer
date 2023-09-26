<#-- @ftlvariable name="stages" type="com.cognizant.leap.model.StageConverter" -->

${config}
<#list stages as stage>
    {
      "id": "${stage.getId()}",
      "type": "${stage.getType()}",
      "desc": "${stage.getDescription()}",
      "toolId": "${stage.getToolId()}",
      "parallel": ${stage.isParallel()?c},
      "data":
      <#if stage.isChildStage()>
          ${stage.getChildStageDate()}
      <#else>
        {
          ${stage.getStageData()},
          <#if stage.isStep()>
             ${stage.getStep_ftl()},
          <#else>
            "steps" : [],
          </#if>
          "additionalProperties": {}
        }
      </#if>

      <#if stage.getIsEnvironment()>
        ,
        ${stage.getEnvironment_ftl()}
      </#if>

       <#if stage.getIsInput()>
        ,
        ${stage.getInput_ftl()}
       </#if>

       <#if stage.getIsOptions()>
        ,
        ${stage.getOptions_ftl()}
       </#if>

       <#if stage.getIsWhen()>
        ,
        ${stage.getWhen_ftl()}
       </#if>

      <#if stage.isSource()>
        ,
        ${stage.getSource_ftl()}
      </#if>

      <#if stage.isAgent()>
        ,
        ${stage.getAgent_ftl()}
      </#if>
    }
    <#if stage?has_next>
    ,
    </#if>
</#list>