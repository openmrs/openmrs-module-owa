<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Open Web Apps" otherwise="/login.htm" redirect="/module/owa/manage.form" />
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
    var file=document.forms["Form"]["file"].value;   
    if (file==null || file=="") {
      alert("<spring:message code="owa.attach_zip"/>");
      return false;
      }
    }
</script>

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
    
    table{
        width:100%;
    }
    	
</style>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/jquery-2.1.3.min.js"/>
<openmrs:htmlInclude file="/moduleResources/owa/javascript/deleteApp.js"/>

<c:if test="${not empty error}">
<div id="openmrs_error"><c:out value="${error}"></c:out></div>
</c:if>

<c:if test="${not empty message}">
<div id="openmrs_msg"><c:out value="${message}"></c:out></div>
</c:if>

<div>
    <c:choose>
        <c:when test="${settingsValid == true}">
            <div id="uploadArea">
                <form id="uploadPackageForm" enctype="multipart/form-data" method="post" name="Form" onsubmit="return validateForm()" action="addApp.htm">
                    <span style="margin-right: 30px"><spring:message code="owa.upload_app_package" />:</span>
                    <input type="file" id="file" name="file" accept="application/zip,.zip" />
                    <input type="submit" value="Upload" style="margin-left: 150px;" />
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
            <div>
            <b class="boxHeader">Manage Apps</b>
                <c:forEach items="${appList}" var="app">
                <div class="box" id="AppListing">
                <table>
                  <thead>
                    <tr>                     
                        <th>Logo</th>
                        <th>Name</th>
                        <th>Developer</th>
                        <th>Version</th> 
                        <th></th>                       
                    </tr>
                  </thead>
                  <tbody style="cursor:pointer;">
                    <tr>                      
                        <td onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'">
                         <img style="max-height:48px;max-width:48px;" src="${appBaseUrl}/${app.folderName}/${app.icons.icon48}"></td>            
                        <td onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'" valign="top">${app.name} </td>
                        <td onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'" valign="top">${app.developer.name}</td>
                        <td onclick="location.href = '${appBaseUrl}/${app.folderName}/${app.launchPath}'" valign="top"> ${app.version}</td>              
                        <td valign="top"><input type="button" value="Delete" onclick="deleteApp('${app.name}')" type="image"></td>
                    </tr>
                  </tbody>    
                </table>
                </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</ul>

<%@ include file="/WEB-INF/template/footer.jsp"%>