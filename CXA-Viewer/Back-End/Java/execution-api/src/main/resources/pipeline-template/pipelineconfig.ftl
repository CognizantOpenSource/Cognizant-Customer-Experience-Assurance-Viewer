<#-- @ftlvariable name="config" type="com.cognizant.leap.model.ConfigurationConverter" -->

{
  "id": "${config.getId()}",
  "type": "${config.getType()}",
  "desc": "${config.getDescription()}",
  "toolId": "${config.getToolId()}",
  "parallel": ${config.isParallel()?c},
  "data": {
    "type": "pipeline",
    "steps": [],
    "additionalProperties": {}
  }
},