<%@tag description="Shift Logs Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<ul>
    <li${'/shift-logs/cebaf' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/shift-logs/cebaf">CEBAF</a>
    </li>
    <li${'/shift-logs/lerf' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/shift-logs/lerf">LERF</a>
    </li>
    <li${'/shift-logs/uitf' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/shift-logs/uitf">UITF</a>
    </li>
    <li${'/shift-logs/laso' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/shift-logs/laso">LASO</a>
    </li>
</ul>