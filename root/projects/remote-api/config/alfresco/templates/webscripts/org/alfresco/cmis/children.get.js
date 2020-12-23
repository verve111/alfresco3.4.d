<import resource="classpath:alfresco/templates/webscripts/org/alfresco/cmis/lib/read.lib.js">

script:
{
    // locate node
    var object = getObjectFromUrl();
    if (object.node == null)
    {
        break script;
    }
    model.node = object.node;

    // handle filters
    model.types = args[cmis.ARG_TYPES] === null ? cmis.defaultTypesFilter : args[cmis.ARG_TYPES];
    if (!cmis.isValidTypesFilter(model.types))
    {
        status.code = 400;
        status.message = "Types filter '" + model.types + "' unknown";
        status.redirect = true;
        break script;
    }
    
    // property filter
    model.filter = args[cmis.ARG_FILTER];
    if (model.filter === null)
    {
        model.filter = "*";
    }
    
    // rendition filter
    model.renditionFilter = args[cmis.ARG_RENDITION_FILTER];
    if (model.renditionFilter === null || model.renditionFilter.length == 0)
    {
        model.renditionFilter = "cmis:none";
    }

    // order by
    var orderBy = args[cmis.ARG_ORDER_BY];
    
    // include allowable actions
    var includeAllowableActions = args[cmis.ARG_INCLUDE_ALLOWABLE_ACTIONS];
    model.includeAllowableActions = (includeAllowableActions == "true" ? true : false);
    
    // include relationships
    model.includeRelationships = args[cmis.ARG_INCLUDE_RELATIONSHIPS];
    if (model.includeRelationships == null || model.includeRelationships.length == 0)
    {
        model.includeRelationships = "none";
    }    

    // retrieve children
    var page = paging.createPageOrWindow(args);
    var paged = cmis.queryChildren(model.node, model.types, orderBy, page);
    model.results = paged.results;
    model.cursor = paged.cursor;
}