<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Teams"/>
<t:inventory-page title="${title}">
    <jsp:attribute name="stylesheets">
            <link rel="stylesheet" type="text/css"
                  href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/teams.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
            <script type="text/javascript"
                    src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/teams.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <div>
                <c:set var="readonly" value="${true}"/>
                <c:if test="${not readonly}">
                    <s:editable-row-table-controls>
                    </s:editable-row-table-controls>
                </c:if>
                <div>
                    <table id="teams-table" class="data-table stripped-table ${readonly ? '' : 'uniselect-table editable-row-table'}">
                        <thead>
                        <tr>
                            <th>Order</th>
                            <c:if test="${empty param.name}">
                                <th>Name</th>
                            </c:if>
                            <c:if test="${pageContext.request.isUserInRole('presenter-admin')}">
                                <th>Directory Role</th>
                            </c:if>
                            <th>Members</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${teamList}" var="team">
                            <tr class="draggable-row" draggable="true" data-id="${team.teamId}">
                                <td class="handle-cell"><div class="drag-handle" aria-label="Drag to reorder" title="Drag to reorder">⇅</div></td>
                                <td>
                                    <c:out value="${team.name}"/>
                                    ${team.archived ? '🗄️' : ''}
                                </td>
                                <c:if test="${pageContext.request.isUserInRole('presenter-admin')}">
                                    <td><c:out value="${team.directoryRole}"/></td>
                                </c:if>
                                <td>
                                    <ul class="cell-list">
                                        <c:forEach items="${team.members}" var="user">
                                            <li><c:out value="${s:formatUser(user)}"/></li>
                                        </c:forEach>
                                    </ul>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="footnote">🗄️ Archived</div>
                </div>
            </div>
        </section>
    </jsp:body>
</t:inventory-page>