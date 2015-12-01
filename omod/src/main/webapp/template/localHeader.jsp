<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin">
            <spring:message code="admin.title.short" />
        </a>
    </li>

    <li <c:if test='<%= request.getRequestURI().contains("/manage") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/owa/manager.form">
            <spring:message code="owa.manage" />
        </a>
    </li>

    <!-- Add further links here -->
    <li <c:if test='<%= request.getRequestURI().contains("/settings") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/owa/settings.form">
            <spring:message code="owa.settings" />
        </a>
    </li>
    
</ul>
<h4>
    <spring:message code="owa.title" />
</h4>
