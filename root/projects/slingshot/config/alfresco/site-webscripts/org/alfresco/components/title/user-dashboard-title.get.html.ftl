<#assign activePage = page.url.templateArgs.pageid!"customise-user-dashboard">
<#assign userName>${user.properties["firstName"]?html} <#if user.properties["lastName"]??>${user.properties["lastName"]?html}</#if></#assign>
<script type="text/javascript">//<![CDATA[
   Alfresco.constants.DASHLET_RESIZE = ${((page.url.templateArgs.userid!"-") = (user.name!""))?string};
//]]></script>
<div class="page-title theme-bg-color-1 theme-border-1"><!-- ${activePage} -->
   <div class="title">
      <h1 class="theme-color-3">${msg("header.dashboard", "<span>${userName}</span>")}</h1>
   </div>
   <div class="links title-button">
      <#assign linkClass><#if "customise-user-dashboard" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/customise-user-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
         </span>
      </span>          

   </div>
</div>