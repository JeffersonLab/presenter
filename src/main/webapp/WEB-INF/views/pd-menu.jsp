<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Program Deputy (PD) Presentations"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            #recent-table {
                border-collapse: collapse;
            }
            #recent-table, 
            #recent-table td, 
            #recent-table th {
                border-bottom: 1px solid black;
            }
            #recent-table td {
                padding: 8px;
            }
            #recent-table tbody td:last-child button {
                margin-left: 15px;
            }
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
        <script type="text/javascript">
            $(document).on("click", ".open-link", function() {
                $(this).closest("form").submit();
                return false;
            });
        </script>
    </jsp:attribute>            
    <jsp:body>
        <section>
            <div>
                <form action="pd-menu" method="post">
                    <dl>                   
                        <dt><label for="type">Presentation Type</label></dt>
                        <dd>
                            <select id="type" name="type">
                                <option value=""> </option>
                                <c:forEach items="${typeList}" var="type">
                                    <option value="${type.name()}"${param.type eq type or (param.type eq null and defaultType eq type) ? ' selected="selected"' : ''}><c:out value="${type.description}"/></option>                                    
                                </c:forEach>
                            </select>
                            <span class="error">${messages.type}</span>
                        </dd>
                        <dt><label for="date">Delivery Date</label></dt>
                        <dd>
                            <fmt:formatDate pattern="yyyy-MM-dd" var="dateStr" value="${defaultDate}"/>
                            <input id="date" name="date" class="datepicker" type="text" value="${empty dateStr ? param.date : dateStr}"/>
                            <span class="error">${messages.date}</span>
                            <span class="format">(yyyy-mm-dd)</span>
                        </dd>
                    </dl>
                    <button type="submit" value="Open" name="action">Open / Create</button>
                    <button type="submit" value="Export PDF" name="action">Export PDF</button>
                    <c:if test="${pageContext.request.isUserInRole('oability')}">
                        <button type="submit" value="Delete" name="action" onclick="return confirm('Are you sure you want to delete this presentation?')">Delete</button>
                    </c:if>
                    <c:if test="${param.elogId ne null}">
                        <c:choose>
                            <c:when test="${param.elogId > 0}">
                                <span class="success">Log Entry <a target="_blank" href="https://logbooks.jlab.org/entry/${param.elogId}">${param.elogId}</a> Saved</span>                            
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
            <c:choose>
                <c:when test="${fn:length(recentList) == 0}">
                    <div class="message-box">No Recent Presentations!</div>
                </c:when>
                <c:otherwise>
                    <div>
                        <h3>Recent Presentations (Last 8 Days)</h3>
                        <table id="recent-table">
                            <thead>
                                <tr>
                                    <th>Type</th>
                                    <th>Delivery Date</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentList}" var="recent">
                                    <tr>
                                        <td><c:out value="${recent.pdPresentationType}"/></td>
                                        <td><fmt:formatDate pattern="yyyy-MM-dd" value="${recent.deliveryYmd}" var="fmtDelivery"/><c:out value="${fmtDelivery}"/></td>
                                        <td>
                                            <form method="post" action="pd-menu">
                                                <input type="hidden" name="type" value="${recent.pdPresentationType}"/>
                                                <input type="hidden" name="date" value="${fmtDelivery}"/>
                                                <a class="open-link" href="#">Open</a>
                                            </form>                                            
                                        </td>
                                        <td>
                                            <c:if test="${pageContext.request.isUserInRole('pd')}">
                                                <form method="post" action="pd-menu">
                                                    <input type="hidden" name="type" value="${recent.pdPresentationType}"/>
                                                    <input type="hidden" name="date" value="${fmtDelivery}"/>
                                                    <button type="submit" value="Delete" name="action" onclick="return confirm('Are you sure you want to delete this ${recent.pdPresentationType} ${fmtDelivery} presentation?')">Delete</button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
                    <div>
                        <h3>Presentations by Category</h3>
                        <ul>
                            <li><a href="pd-morning?date=${empty dateStr ? param.date : dateStr}">Today Morning</a> | (<a href="pd-morning?date=${empty dateStr ? param.date : dateStr}&amp;pdf=Y">PDF</a>)</li>
                            <li><a href="pd-afternoon?date=${empty dateStr ? param.date : dateStr}">Today Afternoon</a> | (<a href="pd-afternoon?date=${empty dateStr ? param.date : dateStr}&amp;pdf=Y">PDF</a>)</li>
                        </ul>
                    </div>
            <div>
                <h3>Weekly Summary Notes</h3>
                <ul>
                    <li>When PD tenures overlap, the Outgoing PD imports the Oncoming PD's slides to create a consolidated presentation (button is located on editing page)</li>
                </ul>
            </div>
            <div>
                <h3>Log Entry Notes</h3>
                <ul>
                    <li>Each morning at 8:30 AM an Elog entry is automatically created for each 8:00 AM presentation that exists (please avoid creating multiple types)</li>
                </ul>
            </div>                
        </section>
    </jsp:body>         
</t:page>      
