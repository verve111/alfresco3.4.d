{
    data:
    {
        "id" : "${collection.id}",
        "name" : "${collection.name}",
        "title" : "${collection.title}",
        "description" : "${collection.description}",
        "assets" :
        [
        <#list collection.assetIds as assetId>
            "${assetId}"<#if assetId_has_next>,</#if>
        </#list>    
        ]
    }
}