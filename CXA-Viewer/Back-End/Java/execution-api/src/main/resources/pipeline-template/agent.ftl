<#-- @ftlvariable name="agent" type="com.cognizant.leap.model.AgentConverter" -->

  "agent": {
    "id": "${agent.getId()}",
    "name": "${agent.getName()}",
    "type": "${agent.getType()}",
    "agentType": "${agent.getAgentType()}",
    "data": {
      <#if agent.getIsAgentData()>
      ${agent.getAgentData()},
      </#if>
    }
  }