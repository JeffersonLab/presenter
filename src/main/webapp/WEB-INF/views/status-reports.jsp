<%@taglib prefix="sql" uri="jakarta.tags.sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<c:set var="title" value="Status Reports"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/status-reports.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
            <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/status-reports.js"></script>
    </jsp:attribute>        
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true" resetButton="false" ribbon="true">
                <form class="filter-form" method="get" action="status-reports">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Time</legend>
                            <s:date-range datetime="${true}" sevenAmOffset="${true}"/>
                        </fieldset>
                        <fieldset>
                            <legend>Taxonomy</legend>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="team-select">Team</label>
                            </div>
                            <div class="li-value">
                                <select id="team-select" name="teamId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${teamList}" var="team">
                                        <option value="${team.teamId}"${param.teamId eq team.teamId ? ' selected="selected"' : ''}>
                                            <c:out value="${team.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </ul>
                </fieldset>
                    </div>
                    <input type="hidden" class="offset-input" name="offset" value="0"/>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <c:if test="${fn:length(teamStatusReportList) > 0}">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Day / Team</th>
                        <th>Accomplished</th>
                        <th>In Progress</th>
                        <th>Planned</th>
                        <th>Roadblocks</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach items="${teamStatusReportList}" var="report">
                    <tr>
                        <td>
                            <div><fmt:formatDate pattern="dd-MMM-yyyy" value="${report.ymd}"/></div>
                            <div><c:out value="${report.team.name}"/></div>
                        </td>
                        <td><c:out value="${report.accomplished}"/></td>
                        <td><c:out value="${report.inProgress}"/></td>
                        <td><c:out value="${report.planned}"/></td>
                        <td><c:out value="${report.roadblocks}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <button class="previous-button" type="button" data-offset="${paginator.previousOffset}"
            value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
            </button>
            <button class="next-button" type="button" data-offset="${paginator.nextOffset}"
            value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
            </button>
            </c:if>
        </section>
    </jsp:body>         
</s:page>
