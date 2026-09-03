<%@tag description="Primary Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<ul>
    <li${'/presentations' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/presentations">Presentations</a>
    </li>
    <li${fn:startsWith(currentPath, '/shift-logs') ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/shift-logs/cebaf">Shift Logs</a>
    </li>
    <li${'/status-reports' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/status-reports">Status Reports</a>
    </li>
    <c:if test="${pageContext.request.isUserInRole('template-admin')}">
        <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}>
            <a href="${pageContext.request.contextPath}/setup/settings">Setup</a>
        </li>
    </c:if>
    <li${'/help' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/help">Help</a>
    </li>
</ul>