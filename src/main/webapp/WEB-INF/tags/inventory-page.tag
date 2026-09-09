<%@tag description="The Inventory Page Template" pageEncoding="UTF-8"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <h2 id="left-column-header">Inventory</h2>
        <ul>
            <li${fn:startsWith(currentPath, '/inventory/teams') ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/teams">Teams</a></li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>         
</s:page>
