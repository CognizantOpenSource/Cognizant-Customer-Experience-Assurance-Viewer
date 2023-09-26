<#-- @ftlvariable name="source" type="com.cognizant.leap.model.SourceConverter" -->

 "source": {
    "id": "${source.getId()}",
    "type": "${source.getType()}",
    "name": "${source.getName()}",
    "data": {
      "type": "${source.getDataType()}",
      "repo": "${source.getRepo()}",
      "branch": "${source.getBranch()}",
      "subDirectory": "${source.getSubDirectory()}"
    },
    "additionalProperties": {}
  }