<#-- @ftlvariable name="steps" type="com.cognizant.leap.model.StepConverter" -->

"steps": [
    <#list steps as step>
        {
          "type" : "${step.getType()}",
          "platform" : "${step.getPlatform()}",
          <#if (step.getPlatforms()?size > 0)>
          "platforms" : [
              ${step.getPlatforms()}
          ],
          </#if>
          "data": {
               "script" : [
                    ${step.getStepCoreData()}
               ]
          },
          "changeDir": ""
        }
       <#if step?has_next>
         ,
       </#if>
   </#list>
  ]