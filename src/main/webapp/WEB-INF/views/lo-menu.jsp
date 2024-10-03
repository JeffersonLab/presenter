<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="LERF Operator (LO) Presentations"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            #notification-bar {
                position: fixed;
                background-color: orange;
                color: white;
                width: 100%;
                text-align: center;
                font-size: 24px;
                box-shadow: 0 8px 8px #979797;
                z-index: 1000;  
                font-family: "Verdana",sans-serif;
            }     
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">              
    </jsp:attribute>            
    <jsp:body>
        <section>
            <div>
                <h3>LERF SHIFT LOG</h3>
                <form action="lo-menu" method="post">
                    <dl>                   
                        <dt><label for="date">Date</label></dt>
                        <dd>
                            <fmt:formatDate pattern="yyyy-MM-dd" var="dateStr" value="${defaultDate}"/>
                            <input id="date" name="date" class="datepicker" type="text" value="${empty dateStr ? param.date : dateStr}"/>
                            <span class="error">${messages.date}</span>
                            <span class="format">(yyyy-mm-dd)</span>
                        </dd>
                        <dt><label for="shift">Shift</label></dt>
                        <dd>
                            <select id="shift" name="shift">
                                <option value=""> </option>
                                <c:forEach items="<%= org.jlab.presenter.persistence.enumeration.Shift.values()%>" var="shift">
                                    <option${param.shift eq shift or defaultShift eq shift ? ' selected="selected"' : ''}><c:out value="${shift}"/></option>
                                </c:forEach>
                            </select>
                            <span class="error">${messages.shift}</span>
                        </dd> 
                    </dl>
                    <button type="submit" value="Open" name="action">Open</button>                   
                    <button type="submit" value="Export PDF" name="action">Export PDF</button>
                    <c:if test="${param.elogId ne null}">
                        <c:choose>
                            <c:when test="${param.elogId > 0}">
                                <span class="success">Log Entry <a target="_blank" href="https://logbooks.jlab.org/entry/${fn:escapeXml(param.elogId)}"><c:out value="${param.elogId}"/></a> Saved</span>
                            </c:when>
                            <c:when test="${param.elogId eq 0}">
                                <span class="success">Log Entry Queued</span>                            
                            </c:when>                            
                            <c:otherwise>
                                <span class="error">Unable to send to eLog</span>                            
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <span class="error"><c:out value="${messages.error}"/></span> 
                </form>
            </div>
        </section>
    </jsp:body>         
</t:page>      
