function sortByTitle(link1, link2)
{
   return (link1.title > link2.title) ? 1 : (link1.title < link2.title) ? -1 : 0;
}

function main()
{
   var site, container, theUrl, connector, result, links;
   
   site = page.url.templateArgs.site;
   container = 'links';
   theUrl = '/api/links/site/' + site + '/' + container + '?page=1&pageSize=512';
   connector = remote.connect("alfresco");
   result = connector.get(theUrl);
   if (result.status == 200)
   {
      links = eval('(' + result.response + ')').items;
      links.sort(sortByTitle);
      model.links = links;
      model.numLinks = links.length;
   }
}

main();