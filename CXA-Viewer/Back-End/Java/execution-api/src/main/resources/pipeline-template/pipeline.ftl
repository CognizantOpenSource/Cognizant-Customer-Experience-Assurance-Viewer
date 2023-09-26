<#-- Pipeline Leap Json -->
<#-- @ftlvariable name="pipeline" type="com.cognizant.leap.model.PipelineConverter" -->
<#assign aDate = pipeline.getCreationDate()?date>
{
    "version" : "${pipeline.getVersion()}",
    "name" : "${pipeline.getName()}",
    "platform" : "${pipeline.getPlatform()}",
    "creationDate" : "${aDate?iso_local}",
    "ci" : {
        "pipeline" : {
            <#if pipeline.getIsAgent()>
                 ${pipeline.getAgent_ftl()},
            </#if>

           <#if pipeline.getIsEnvironment()>
                 ${pipeline.getEnvironment_ftl()},
            </#if>

           <#if pipeline.getIsTools()>
                 ${pipeline.getTools_ftl()},
            </#if>

           <#if pipeline.getIsTriggers()>
                 ${pipeline.getTriggers_ftl()},
            </#if>

            "stages" : [
                 ${pipeline.getStages_ftl()}
            ]
            <#if pipeline.getIsPost()>
                ,
                ${pipeline.getPost_ftl()}
            </#if>

            <#if pipeline.getIsOptions()>
                ,
                 ${pipeline.getOptions_ftl()}
            </#if>

            <#if pipeline.getIsParameters()>
                ,
                 ${pipeline.getParameters_ftl()}
            </#if>
        },
        "additionalProperties" : {}
    },
    "additionalProperties" : {}
}

