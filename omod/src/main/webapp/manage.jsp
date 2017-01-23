<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage OWA" otherwise="/login.htm" redirect="/module/owa/manage.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<%@ page import="org.openmrs.web.WebConstants" %>
<%
pageContext.setAttribute("message", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
pageContext.setAttribute("error", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
%>

<script type="text/javascript">
    function validateForm() {
        var file = document.forms["Form"]["file"].value;
        if (file == null || file == "") {
            alert("<spring:message code="owa.attach_zip"/>");
            return false;
        }
    }
</script>

<openmrs:htmlInclude file="/moduleResources/owa/css/app.css"/>
<openmrs:htmlInclude file="/moduleResources/owa/css/bootstrap.min.css"/>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/jquery-2.1.3.min.js"/>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/deleteApp.js"/>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/bootstrap-filestyle.min.js"/>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/jquery.dataTables.min.js"/>

<c:if test="${not empty error}">
    <div id="openmrs_error"><c:out value="${error}"></c:out></div>
</c:if>

<c:if test="${not empty message}">
    <div id="openmrs_msg"><c:out value="${message}"></c:out></div>
</c:if>

<div>
    <br/>
    <c:choose>
        <c:when test="${allowAdmin}">
            <div id="uploadArea" class="divTitle">
                <form name="uploadPackageForm" enctype="multipart/form-data" method="post" name="Form" onsubmit="return validateForm()" action="addApp.htm">

                    <h1><span><spring:message code="owa.upload_app_package" />:</span></h1>
                    <div class="input-group">
                        <span class="input-group-btn" id="fileSpan">
                            <input type="file" id="file" name="file" accept="application/zip,.zip" >
                        </span>
                        <span class="input-group-btn" id="uploadSpan">
                            <button type="submit" class="btn btn-primary btn-file">
                                <span class="glyphicon glyphicon-upload"></span> Upload
                            </button>
                        </span>
                        <span class="input-group-btn" id="clearSpan">
                            <button id="clear" class="btn btn-primary btn-file" type="button"> Clear </button>
                        </span>
                    </div>
                </form>
            </div>
            <div id="progressbar"></div>
        </c:when>
        <c:when test="${!settingsValid}">
            <div id="uploadArea" class="divTitle">Please configure the <a href="../../module/owa/settings.form">app settings</a> before installing apps</div>
        </c:when>
    </c:choose>
</div>

<script type="text/javascript">
    $('#file').filestyle({
        buttonText: ' Browse ',
        buttonName: 'btn-primary'
    });

    $('#clear').click(function () {
        $('#file').filestyle('clear');
    });
</script>

<c:if test="${empty appStoreUrl}">
    <div id="appStoreLink">Look for more apps in the <a href="${appStoreUrl}" target="_blank">app store</a></div>
</c:if>

<div class="appList">
    <c:choose>
        <c:when test="${empty appList}">
            <span><br/><spring:message code="owa.you_have_no_apps_installed" /></span>
            </c:when>
            <c:otherwise>
            <div class="divTitle">
                <h1><span>Manage Apps:</span></h1>
                <table width="100%" class="table table-striped table-hover table-condensed" id="sort">
                    <thead>
                        <tr>                     
                            <th>Logo</th>
                            <th id="sorting">Name</th>
                            <th id="sorting">Developer</th>
                            <th>Version</th> 
                            <th>Delete</th>                       
                        </tr>
                    </thead>
                    <tbody style="cursor:pointer;">
                        <c:forEach items="${appList}" var="app">


                            <tr>                      
                                <td onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'">
                                    <img style="height:48px;width:48px;" src="${appBaseUrl}/${app.folderName}/${app.icons.icon48}"></td>            
                                <td width="65%" align="top"onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'" valign="top">
                                    <span style="font-weight: bold">${app.name}</span> </br> ${app.description}
                                </td>
                                <td width="20%" valign="top" onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'" valign="top">${app.developer.name}</td>
                                <td width="5%" valign="top" onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'" valign="top"> ${app.version}</td>              
                                <td style="text-align: center">
                                    <c:choose>
                                        <c:when test="${allowAdmin}">
                                            <button class="btn btn-primary" onclick="deleteApp('${app.name}')">
                                                <span class="glyphicon glyphicon-remove"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn btn-primary disabled" title="<spring:message code="owa.settings_not_allowed"/>">
                                                <span class="glyphicon glyphicon-lock"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>

                        </c:forEach>
                    </tbody>    
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<script type="text/javascript">
$(document).ready(function() {
    $('#sort').DataTable( {
        "order": [[ 3, "desc" ]]
    } );
} );
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>
