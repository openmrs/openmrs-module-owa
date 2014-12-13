<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}! 5666</p>


<style type="text/css">
    #uploadArea {
        border: 1px solid #ccc; 
        border-radius: 3px; 
        padding: 10px; 
        width: 440px; 
        margin-bottom: 30px;
    }

    #progressbar {
        width: 460px;
        margin-bottom: 30px;
    }

    #appStoreLink {
        font-size: 15px;
        color: #777;
        border-bottom: 1px solid #ccc;
        padding-bottom: 10px;
        width: auto;
        max-width: 460px;
    }
</style>

<div>
    <%--#if( ${settingsValid} == true )--%>
    <div id="uploadArea">
        <form id="uploadPackageForm" enctype="multipart/form-data" method="post">
            <span style="margin-right: 30px"><spring:message code="owa.upload_app_package" />:</span>
            <input type="file" id="upload" name="upload" accept="application/zip,.zip" />
        </form>
    </div>
    <div id="progressbar"></div>
    <%--#else--%>
    <div id="uploadArea">Please configure the <a href="appSettings.action">app settings</a> before installing apps</div>
    <%--#end--%>
</div>

<%--#if( $!appStoreUrl )--%>
<div id="appStoreLink">Look for more apps in the <a href="${appStoreUrl}" target="_blank">app store</a></div>
<%--#end--%>

<ul class="introList">
    <%--#if( $appList.isEmpty() )--%>
    <li style="margin-left: 15px; margin-top: 6px;"><spring:message code="owa.you_have_no_apps_installed" /></li>
        <%--#else--%>
        <%--#foreach( $app in $appList )--%>
    <li class="introItem" onclick="window.location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'">
        <span class="introItemHeader">
            <img style="float:left; margin-right:15px" src="${appBaseUrl}/${app.folderName}/${app.icons.icon48}">
            ${app.name}
        </span><br>
        <spring:message code="owa.author" />: ${app.developer.name}<br>
        <spring:message code="owa.version" />: ${app.version}
    </li>
    <%--#end
    #end--%>
</ul>


<%@ include file="/WEB-INF/template/footer.jsp"%>