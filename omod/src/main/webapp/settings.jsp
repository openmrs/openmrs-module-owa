<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage OWA" otherwise="/login.htm" redirect="/module/owa/settings.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/owa/css/app.css"/>
<openmrs:htmlInclude file="/moduleResources/owa/css/bootstrap.min.css"/>

<spring:hasBindErrors name="globalPropertiesModel">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>
<br/>
<div id="settingsDiv" class="divTitle">
    <h1><span><spring:message code="owa.settings" />:</span></h1>
    <c:choose>
        <c:when test="${allowAdmin}">
            <form:form method="post"  modelAttribute="globalPropertiesModel">
                <table width="100%" class="table table-striped table-hover table-condensed">
                    <tbody style="cursor:pointer;">
                    <c:forEach var="prop" items="${globalPropertiesModel.properties}" varStatus="varStatus">
                        <tr>
                            <td>
                                <spring:nestedPath path="properties[${varStatus.index}]">
                                    <div class="settingRow">
                                        <h4 class="settingName"><spring:message code="${prop.property}.label" /></h4>
                                        <span class="settingValue">
                                        <spring:bind path="propertyValue">
                                            <c:set var="inputSize" value="50" scope="page" />
                                            <input type="text" name="${status.expression}" value="${status.value}" size="${inputSize}">
                                            <form:errors cssClass="error"/>
                                        </spring:bind>
                                    </span>
                                        <span class="settingDescription">
                                                ${prop.description}
                                        </span>
                                    </div>
                                </spring:nestedPath>
                            </td>
                        </tr>
                    </c:forEach>

                    </tbody>
                </table>
                <div class="settingRow">
                    <div class="saveButtons">
                        <input type="submit" value='<spring:message code="general.save"/>' />
                        <input type="button" value='<spring:message code="general.cancel"/>'
                               onclick="javascript:window.location = '<openmrs:contextPath />/admin'" />
                    </div>
                </div>
            </form:form>
        </c:when>
        <c:otherwise>
            <spring:message code="owa.settings_not_allowed"/>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
