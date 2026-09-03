<%@taglib prefix="sql" uri="jakarta.tags.sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<c:set var="title" value="Status Reports"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>
                <fieldset>
                    <legend>Step 1:</legend>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label class="required-field" for="group-select">Team</label>
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
            <c:choose>
                <c:when test="${componentList eq null}">
                    <div class="message-box">Select a group to continue</div>
                </c:when>
                <c:otherwise>
                    <div class="message-box">
                        <c:out value="${selectionMessage}"/>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </jsp:body>         
</s:page>
