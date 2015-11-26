<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Open Web Apps" otherwise="/login.htm"
                 redirect="/module/owa/settings.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<%@ page import="org.openmrs.web.WebConstants" %>
<%
pageContext.setAttribute("message", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
pageContext.setAttribute("error", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
%>

<openmrs:htmlInclude file="/moduleResources/owa/css/app.css"/>
<openmrs:htmlInclude file="/moduleResources/owa/css/bootstrap.min.css"/>

<c:if test="${not empty error}">
    <div id="openmrs_error"><c:out value="${error}"></c:out></div>
</c:if>

<c:if test="${not empty message}">
    <div id="openmrs_msg"><c:out value="${message}"></c:out></div>
</c:if>

<div id="settingsDiv" class="divTitle">
    <h1><span><spring:message code="owa.settings" />:</span></h1>
    <form method="post">
        <table width="100%" class="table table-striped table-hover table-condensed">
            <tbody style="cursor:pointer;">
                <c:forEach var="globalProp" items="${globalProps}" varStatus="status">
                    <tr>
                        <td>
                            <div class="settingRow">
                                <h4 class="settingName">
                                    ${globalProp.property}
                                </h4>

                                <span class="settingValue">
                                    <input type="text" size="60" name="PROP_VAL_NAME" value="${globalProp.propertyValue}" size="30" maxlength="4000" />
                                </span>
                                <span class="settingDescription">${globalProp.description}
                                </span>
                            </div>

                        </c:forEach>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="settingRow">
            <div class="saveButtons">
                <input type="submit" value='<spring:message code="general.save"/>' />
                <input type="button" value='<spring:message code="general.cancel"/>'
                       onclick="javascript:window.location = '<openmrs:contextPath />/admin'" />
            </div>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
