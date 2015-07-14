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
	padding: 0.20em;
	background-color: #B6B3B2;
}

.settingValue {
	float: left;
	width: 40%;
}

.settingDescription {
	font-size: 0.8em;
	float: left;
	width: 40%;
}

.saveButtons {
	padding-left: 0.5em;
	background-color: #B6B3B2;
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
				<input name="PROP_NAME" value="${globalProp.property}" readonly="readonly" / >
			</h4>
			<span class="settingValue"> <c:choose>
					<c:when test="${fn:length(globalProp.propertyValue) > 20}">
						<textarea name="PROP_VAL_NAME" rows="4" cols="50" >${globalProp.propertyValue}</textarea>
					</c:when>
					<c:otherwise>
						<input type="text" name="PROP_VAL_NAME"
							value="${globalProp.propertyValue}" size="30" maxlength="4000" />
					</c:otherwise>
				</c:choose>
			</span> 
			<span class="settingDescription"> 
			<textarea name="PROP_DESC_NAME" readonly="readonly" rows="4" cols="100">${globalProp.description}</textarea>
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
