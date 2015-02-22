<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Open Web Apps" otherwise="/login.htm" redirect="/module/owa/manage.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

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
    .introItem{
        padding: 20px 10px;
        width: 150px;
        min-height: 90px;
        vertical-align: top;
        line-height: 1.3em;
        margin: 10px;
        text-align: center;
    }
</style>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/jquery-2.1.3.min.js"/>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/deleteApp.js"/>

<div>
    <c:choose>
        <c:when test="${settingsValid == true}">
            <div id="uploadArea">
                <form id="uploadPackageForm" enctype="multipart/form-data" method="post" action="addApp.htm">
                    <span style="margin-right: 30px"><spring:message code="owa.upload_app_package" />:</span>
                    <input type="file" id="file" name="file" accept="application/zip,.zip" />
                    <input type="submit"/>
                </form>
            </div>
            <div id="progressbar"></div>
        </c:when>
        <c:otherwise>
            <div id="uploadArea">Please configure the <a href="../../admin/maintenance/globalProps.form">app settings</a> before installing apps</div>
        </c:otherwise>
    </c:choose>
</div>

<c:if test="${empty appStoreUrl}">
    <div id="appStoreLink">Look for more apps in the <a href="${appStoreUrl}" target="_blank">app store</a></div>
</c:if>

<ul class="introList">
    <c:choose>
        <c:when test="${empty appList}">
            <li style="margin-left: 15px; margin-top: 6px;"><spring:message code="owa.you_have_no_apps_installed" /></li>
            </c:when>
            <c:otherwise>
                <c:forEach items="${appList}" var="app">
                <table border='0' width="100%">
                    <tr>
                        <td>
                            <span class="introItemHeader">
                                <img style="float:left; margin-right:15px" src="${appBaseUrl}/${app.folderName}/${app.icons.icon48}">
                            </span>
                        </td>
                        <td>
                            <div width="100%" align="left">
                                <a href="${appBaseUrl}/${app.folderName}/${app.launchPath}">${app.name}</a><br/>
                                <spring:message code="owa.author" />: ${app.developer.name}<br>
                                <spring:message code="owa.version" />: ${app.version}
                            </div>
                        </td>
                        <td>
                            <input type="button" value="Delete" style="width:60px" onclick="deleteApp('${app.name}')" />
                        </td>
                    </tr>
                </table>
                <br>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</ul>

<%@ include file="/WEB-INF/template/footer.jsp"%>