<#-- @ftlvariable name="post" type="com.cognizant.leap.model.PipelineConverter" -->

"post" : {
<#list post.getPostList() as post>
     "${post.get("Type")}" : [
          {
              "type" : "groovy-script",
              "platform" : "",
              "data" : {
                  "script" : [
                        ${post.get("Script")}
                  ]
              },
              "changeDir" : ""
          }
     ]
    <#if post?has_next>
    ,
    </#if>
</#list>
}