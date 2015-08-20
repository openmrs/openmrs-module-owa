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

<style type="text/css">
    .settingRow {
        padding-top: 1em;
        clear: both;
    }
    .settingName {
        padding: 0.25em;
        background-color: #‎F0F8FF;
    }
    .settingValue {
        float: left;
        width: 40%;
    }
    .settingDescription {
        font-size: 0.8em;
        float: left;
        width: 55%;
    }
    .saveButtons {
        padding-left: 0.5em;
        background-color: #‎F0F8FF;
        clear: both;
    }
</style>

<c:if test="${not empty error}">
<div id="openmrs_error"><c:out value="${error}"></c:out></div>
</c:if>

<c:if test="${not empty message}">
<div id="openmrs_msg"><c:out value="${message}"></c:out></div>
</c:if>

<h2>
	<spring:message code="owa.settings" />
</h2>

<form method="post">
	<c:forEach var="globalProp" items="${globalProps}" varStatus="status">
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

	<div class="settingRow">
		<div class="saveButtons">
			<input type="submit" value='<spring:message code="general.save"/>' />
			&nbsp;&nbsp; <input type="button" value='<spring:message code="general.cancel"/>'
				onclick="javascript:window.location='<openmrs:contextPath />/admin'" />
		</div>
	</div>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
