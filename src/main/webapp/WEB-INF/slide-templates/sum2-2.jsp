<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="p" uri="http://jlab.org/presenter/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}">
    <div class="title-container">
        <div class="title">PD WEEKLY SUMMARY</div>
    </div>
    <div class="content-container">                       
        <div class="content">
            <div class="flow-container">
                <h2>AVAILABILITY SUMMARY</h2>
                <c:if test="${slide.presentation.presentationType == 'PD_PRESENTATION'}">
                    <fmt:formatDate value="${p:addDays(slide.presentation.deliveryYmd, -7)}" var="startFmt" pattern="dd-MMM-yyyy '07:00'"/>
                    <fmt:formatDate value="${slide.presentation.deliveryYmd}" var="endFmt" pattern="dd-MMM-yyyy '07:00'"/>
                </c:if>
                <c:url var="url" context="/" value="/btm/reports/hall-availability">
                    <c:param name="start" value="${startFmt}"/>
                    <c:param name="end" value="${endFmt}"/>
                    <c:param name="availability-chart" value="table"/>
                    <c:param name="print" value="Y"/>
                    <c:param name="fullscreen" value="Y"/>
                </c:url>
                <div class="table-caption">Table 1: Beam to Halls for the Preceding Week <a title="Show me in BTM" target="_blank" href="${url}">(*)</a></div>
                <div class="table-subcaption">(Wednesday 07:00 - Wednesday 07:00)</div>
                <table class="beam-to-halls-table">
                    <thead>
                        <tr>
                            <th></th>
                            <th>PD Scheduled (hours)</th>
                            <th>Actual Hall Program (hours)</th>
                            <th>Accelerator Availability (%)</th>
                            <th>Acceptable Beam in Use (%)</th>
                            <th>Hall Availability (%)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${slide.beamToHallRecordList}" var="record">
                            <tr>
                                <td><span class="table-field"><c:out value="${record.hall}"/></span></td>
                                <td><span class="table-field editable noformat"><c:out value="${record.scheduled}"/></span></td>
                                <td><span class="table-field editable noformat"><c:out value="${record.actual}"/></span></td>
                                <td><span class="table-field editable noformat"><c:out value="${record.accAvail}"/></span></td>
                                <td><span class="table-field editable noformat"><c:out value="${record.accept}"/></span></td>
                                <td><span class="table-field editable noformat"><c:out value="${record.hallAvail}"/></span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <div class="flow-container">
                <c:url var="url" context="/" value="/btm/reports/beam-time-summary">
                    <c:param name="start" value="${startFmt}"/>
                    <c:param name="end" value="${endFmt}"/>
                    <c:param name="print" value="Y"/>
                    <c:param name="fullscreen" value="Y"/>
                </c:url>
                <div class="table-caption">Table 2: Accelerator-Specific Activities for the Preceding Week <a title="Show me in BTM" target="_blank" href="${url}">(*)</a></div>
                <div class="table-subcaption">(Wednesday 07:00 - Wednesday 07:00)</div>            
                <table class="accelerator-activities-table">
                    <thead>
                        <tr>
                            <th></th>
                            <th>PD Scheduled (hours)</th>
                            <th>Actual (hours)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${slide.accActivityRecordList}" var="record">
                            <tr>
                                <td><span class="table-field"><c:out value="${record.activityType}"/></span></td>                            
                                <td><span class="table-field editable noformat"><c:out value="${record.scheduled}"/></span></td>
                                <td><span class="table-field editable noformat"><c:out value="${record.actual}"/></span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>            
            </div>
        </div>
    </div>
</div>